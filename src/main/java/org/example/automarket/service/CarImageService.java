package org.example.automarket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.CarImageResponseDto;
import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.CarImage;
import org.example.automarket.repo.CarAdRepository;
import org.example.automarket.repo.CarImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarImageService {

    private final CarImageRepository carImageRepository;
    private final CarAdRepository carAdRepository;
    private final FileStorageService fileStorageService;

    /**
     * Bir nechta rasmni bir vaqtning o'zida yuklaydi
     * @param carAdId qaysi e'longa tegishli
     * @param files yuklanayotgan rasmlar ro'yxati
     * @return yuklangan rasmlarning URL lar ro'yxati
     */
    @Transactional
    public List<String> uploadImages(Long carAdId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of(); // bo'sh ro'yxat qaytarish
        }

        CarAd carAd = carAdRepository.findById(carAdId)
                .orElseThrow(() -> new EntityNotFoundException("E'lon topilmadi: " + carAdId));

        List<String> urls = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            // Rasmni diskka saqlash
            String url = fileStorageService.store(file, carAdId);

            CarImage image = CarImage.builder()
                    .carAd(carAd)
                    .imageUrl(url)
                    .isMain(i == 0)           // Birinchi rasm asosiy (main)
                    .orderIndex(i)            // Tartib raqami
                    .build();

            carImageRepository.save(image);
            urls.add(url);
        }

        return urls;
    }

    /**
     * Bitta rasmni o'chirish
     */
    @Transactional
    public void deleteImage(Long imageId) {
        CarImage image = carImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Rasm topilmadi: " + imageId));

        // Diskdan faylni o'chirish
        fileStorageService.delete(image.getImageUrl());

        // Database'dan o'chirish
        carImageRepository.delete(image);
    }



    /**
     * E'lon bo'yicha asosiy rasmni qaytarish (agar kerak bo'lsa)
     */
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