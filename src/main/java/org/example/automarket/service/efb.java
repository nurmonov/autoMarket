//package org.example.automarket.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.example.automarket.entity.CarAd;
//import org.example.automarket.entity.CarImage;
//import org.example.automarket.repo.CarAdRepository;
//import org.example.automarket.repo.CarImageRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CarImageService {
//
//    private final CarImageRepository carImageRepository;
//    private final CarAdRepository carAdRepository;
//    private final CloudinaryService cloudinaryService;  // agar Cloudinary ishlatayotgan bo'lsangiz
//
//    @Transactional
//    public List<String> uploadImages(Long carAdId, List<MultipartFile> files) {
//        CarAd carAd = carAdRepository.findById(carAdId)
//                .orElseThrow(() -> new EntityNotFoundException("E'lon topilmadi"));
//
//        // Agar faqat sotuvchi yoki admin bo'lsa tekshirish qo'shsa bo'ladi
//        List<String> urls = new ArrayList<>();
//
//        for (int i = 0; i < files.size(); i++) {
//            MultipartFile file = files.get(i);
//            String url = cloudinaryService.upload(file);  // yoki local saqlash
//
//            CarImage image = CarImage.builder()
//                    .carAd(carAd)
//                    .imageUrl(url)
//                    .isMain(i == 0)           // birinchi rasm asosiy
//                    .orderIndex(i)
//                    .build();
//
//            carImageRepository.save(image);
//            urls.add(url);
//        }
//
//        return urls;
//    }
//
//    @Transactional
//    public void deleteImage(Long imageId) {
//        CarImage image = carImageRepository.findById(imageId)
//                .orElseThrow(() -> new EntityNotFoundException("Rasm topilmadi"));
//
//        // cloudinaryService.delete(image.getImageUrl());  // agar kerak bo'lsa
//        carImageRepository.delete(image);
//    }
//
//    public List<String> getImagesByCarAd(Long carAdId) {
//        return carImageRepository.findByCarAdIdOrderByOrderIndexAsc(carAdId)
//                .stream()
//                .map(CarImage::getImageUrl)
//                .collect(Collectors.toList());
//    }
//}