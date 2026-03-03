package org.example.automarket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.*;
import org.example.automarket.entity.CarImage;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.repo.CarImageRepository;
import org.example.automarket.service.CarImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/car-images")
@RequiredArgsConstructor
public class CarImageController {

    private final CarImageService carImageService;
    private final AutoMarketMapper mapper;
    private final CarImageRepository carImageRepository;


    @PostMapping(value = "/car-ad/{carAdId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rasmlar muvaffaqiyatli yuklandi"),
            @ApiResponse(responseCode = "400", description = "Fayl topilmadi yoki noto'g'ri format"),
            @ApiResponse(responseCode = "404", description = "E'lon topilmadi")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarImageUploadResponse> uploadImages(
            @PathVariable Long carAdId,
            @RequestParam("images") List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    CarImageUploadResponse.builder()
                            .uploadedCount(0)
                            .message("Hech qanday rasm yuklanmadi")
                            .build()
            );
        }


        List<String> urls = carImageService.uploadImages(carAdId, images);


        List<CarImage> savedImages = carImageRepository.findByCarAdIdOrderByOrderIndexAsc(carAdId);

        List<CarImageResponseDto> dtos = savedImages.stream()
                .map(image -> CarImageResponseDto.builder()
                        .id(image.getId())
                        .carAdId(image.getCarAd().getId())
                        .imageUrl(image.getImageUrl())
                        .isMain(image.isMain())
                        .orderIndex(image.getOrderIndex())
                        .build())
                .collect(Collectors.toList());

        CarImageUploadResponse response = CarImageUploadResponse.builder()
                .uploadedCount(urls.size())
                .images(dtos)
                .imageUrls(urls)
                .message(urls.size() + " ta rasm muvaffaqiyatli yuklandi")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/car-ad/{carAdId}")
    public ResponseEntity<CarImageListResponse> getImages(@PathVariable Long carAdId) {
        List<CarImage> images = carImageRepository.findByCarAdIdOrderByOrderIndexAsc(carAdId);


        List<CarImageResponseDto> dtos;
        try {
            dtos = mapper.toCarImageResponseDtoList(images);
        } catch (Exception e) {

            dtos = images.stream().map(image -> CarImageResponseDto.builder()
                    .id(image.getId())
                    .carAdId(image.getCarAd().getId())
                    .imageUrl(image.getImageUrl())
                    .isMain(image.isMain())
                    .orderIndex(image.getOrderIndex())
                    .build()).collect(Collectors.toList());
        }

        String mainImage = images.stream()
                .filter(CarImage::isMain)
                .findFirst()
                .map(CarImage::getImageUrl)
                .orElse(null);

        CarImageListResponse response = CarImageListResponse.builder()
                .carAdId(carAdId)
                .totalImages(images.size())
                .mainImageUrl(mainImage)
                .images(dtos)
                .build();

        return ResponseEntity.ok(response);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rasm muvaffaqiyatli o'chirildi",
                    content = @Content(schema = @Schema(implementation = CarImageDeleteResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rasm topilmadi")
    })
    @DeleteMapping("/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CarImageDeleteResponse> deleteImage(@PathVariable Long imageId) {
        carImageService.deleteImage(imageId);

        CarImageDeleteResponse response = CarImageDeleteResponse.builder()
                .deletedImageId(imageId)
                .message("Rasm muvaffaqiyatli o'chirildi")
                .success(true)
                .build();

        return ResponseEntity.ok(response);
    }

//    @DeleteMapping("/bulk-delete")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<CarImageDeleteResponse> deleteMultipleImages(
//            @RequestBody @Valid CarImageDeleteRequest request) {
//
//        request.getImageIds().forEach(carImageService::deleteImage);
//
//        CarImageDeleteResponse response = CarImageDeleteResponse.builder()
//                .deletedImageId(null) // bulk bo'lgani uchun null
//                .message(request.getImageIds().size() + " ta rasm muvaffaqiyatli o'chirildi")
//                .success(true)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }


}