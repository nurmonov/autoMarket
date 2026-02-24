package org.example.automarket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
    private final FileStorageService fileStorageService;  // Yangi service

    @Transactional
    public List<String> uploadImages(Long carAdId, List<MultipartFile> files) {
        CarAd carAd = carAdRepository.findById(carAdId)
                .orElseThrow(() -> new EntityNotFoundException("E'lon topilmadi"));

        List<String> urls = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            // Rasmni serverga saqlash
            String url = fileStorageService.store(file, carAdId);

            CarImage image = CarImage.builder()
                    .carAd(carAd)
                    .imageUrl(url)
                    .isMain(i == 0)           // Birinchi rasm asosiy
                    .orderIndex(i)
                    .build();

            carImageRepository.save(image);
            urls.add(url);
        }

        return urls;
    }

    @Transactional
    public void deleteImage(Long imageId) {
        CarImage image = carImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Rasm topilmadi"));

        // Serverdan faylni o'chirish
        fileStorageService.delete(image.getImageUrl());

        carImageRepository.delete(image);
    }

    public List<String> getImagesByCarAd(Long carAdId) {
        return carImageRepository.findByCarAdIdOrderByOrderIndexAsc(carAdId)
                .stream()
                .map(CarImage::getImageUrl)
                .collect(Collectors.toList());
    }
}