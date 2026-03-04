package org.example.automarket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.automarket.dto.CarImageResponseDto;
import org.example.automarket.entity.Brand;
import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.CarImage;
import org.example.automarket.repo.BrandRepository;
import org.example.automarket.repo.CarAdRepository;
import org.example.automarket.repo.CarImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarImageService {

    private final CarImageRepository carImageRepository;
    private final CarAdRepository carAdRepository;
    private final FileStorageService fileStorageService;
    private final BrandRepository brandRepository;

    /**
     * Bir nechta rasmni bir vaqtning o'zida yuklaydi
     * @param carAdId qaysi e'longa tegishli
     * @param files yuklanayotgan rasmlar ro'yxati
     * @return yuklangan rasmlarning URL lar ro'yxati
     */
    @Transactional
    public List<String> uploadImages(Long carAdId, List<MultipartFile> files) {
        // 1. Fayllar bo'sh yoki null bo'lsa — darhol bo'sh ro'yxat qaytaramiz
        if (files == null || files.isEmpty()) {
            log.info("Hech qanday fayl yuklanmadi: carAdId = {}", carAdId);
            return Collections.emptyList();
        }

        // 2. E'lon mavjudligini tekshirish
        CarAd carAd = carAdRepository.findById(carAdId)
                .orElseThrow(() -> new EntityNotFoundException("E'lon topilmadi: ID = " + carAdId));

        // 3. Yuklanadigan rasmlar sonini cheklash (masalan maksimal 10 ta)
        if (files.size() > 10) {
            throw new IllegalArgumentException("Maksimal 10 ta rasm yuklash mumkin");
        }

        List<String> urls = new ArrayList<>(files.size());

        // 4. Har bir faylni alohida yuklash
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            // Fayl bo'sh bo'lsa — o'tkazib yuboramiz
            if (file == null || file.isEmpty()) {
                log.warn("Bo'sh fayl o'tkazib yuborildi: index = {}", i);
                continue;
            }

            try {
                // Rasmni saqlash (universal metod ishlatiladi)
                String url = fileStorageService.store(file, carAdId, FileStorageService.EntityType.CARS);

                // CarImage entity yaratish
                CarImage image = CarImage.builder()
                        .carAd(carAd)
                        .imageUrl(url)
                        .isMain(i == 0)           // Birinchi rasm asosiy bo'ladi
                        .orderIndex(i)            // Tartib raqami (0, 1, 2...)
                        .build();

                // Saqlash
                carImageRepository.save(image);

                urls.add(url);

                log.info("Rasm yuklandi: carAdId={}, url={}, isMain={}", carAdId, url, i == 0);

            } catch (Exception e) {
                log.error("Rasm yuklashda xato: carAdId={}, index={}, sabab={}", carAdId, i, e.getMessage(), e);
                // Xato bo'lsa — keyingi faylga o'tamiz, lekin ro'yxatda ko'rsatmaymiz
            }
        }

        // 5. Agar hech qanday rasm yuklanmagan bo'lsa — xabar qaytarish mumkin
        if (urls.isEmpty()) {
            log.warn("Hech bir rasm muvaffaqiyatli yuklanmadi: carAdId = {}", carAdId);
        } else {
            log.info("{} ta rasm muvaffaqiyatli yuklandi: carAdId = {}", urls.size(), carAdId);
        }

        return urls;
    }



    @Transactional
    public void deleteImage(Long imageId) {
        CarImage image = carImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Rasm topilmadi: " + imageId));

        // Diskdan faylni o'chirish
        fileStorageService.delete(image.getImageUrl());

        // Database'dan o'chirish
        carImageRepository.delete(image);
    }




    @Transactional(readOnly = true)
    public String getMainImage(Long carAdId) {
        return carImageRepository.findByCarAdIdAndIsMainTrue(carAdId)
                .map(CarImage::getImageUrl)
                .orElse(null);
    }
    @Transactional(readOnly = true)
    public List<CarImageResponseDto> getImagesByCarAd(Long carAdId) {
        return carImageRepository.findByCarAdIdOrderByOrderIndexAsc(carAdId)
                .stream()
                .map(this::toResponseDto) // yoki mapper ishlatish
                .collect(Collectors.toList());
    }

    private CarImageResponseDto toResponseDto(CarImage image) {
        return CarImageResponseDto.builder()
                .id(image.getId())
                .carAdId(image.getCarAd().getId())
                .imageUrl(image.getImageUrl())
                .isMain(image.isMain())
                .orderIndex(image.getOrderIndex())
                .build();
    }
}