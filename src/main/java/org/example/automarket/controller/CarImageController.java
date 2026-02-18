//package org.example.automarket.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.automarket.service.CarImageService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/car-images")
//@RequiredArgsConstructor
//public class CarImageController {
//
//    private final CarImageService carImageService;
//
//    @PostMapping("/car-ad/{carAdId}")
//    @PreAuthorize("isAuthenticated()")  // sotuvchi yoki admin bo'lishi kerak
//    public ResponseEntity<List<String>> uploadImages(
//            @PathVariable Long carAdId,
//            @RequestParam("files") List<MultipartFile> files) {
//
//        if (files.isEmpty()) {
//            return ResponseEntity.badRequest().body(List.of("Hech qanday fayl yuklanmadi"));
//        }
//
//        List<String> urls = carImageService.uploadImages(carAdId, files);
//        return ResponseEntity.ok(urls);
//    }
//
//    @DeleteMapping("/{imageId}")
//    @PreAuthorize("hasRole('ADMIN') or @carAdService.isOwnerOfCarAd(#carAdId, authentication.name)")
//    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
//        carImageService.deleteImage(imageId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/car-ad/{carAdId}")
//    public ResponseEntity<List<String>> getImages(@PathVariable Long carAdId) {
//        return ResponseEntity.ok(carImageService.getImagesByCarAd(carAdId));
//    }
//}
