package org.example.automarket.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.*;
import org.example.automarket.entity.enums.AdStatus;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.repo.CarAdRepository;
import org.example.automarket.service.CarAdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarAdController {

    private final CarAdService carAdService;
    private final CarAdRepository carAdRepository;
    private AutoMarketMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<CarAdDetailDto> create(@Valid @RequestBody CarAdCreateRequest request) {
        CarAdDetailDto dto = carAdService.createCarAd(request, List.of());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<CarAdDetailDto> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(carAdService.getCarAdDetail(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<CarAdDetailDto> update(
            @PathVariable Long id,
            @Valid @RequestPart("request") CarAdUpdateRequest request,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        CarAdDetailDto dto = carAdService.updateCarAd(id, request, newImages);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carAdService.deleteCarAd(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/moderate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> moderate(@PathVariable Long id, @Valid @RequestBody ModerateCarAdRequest request) {
        carAdService.moderateCarAd(id, request);
        return ResponseEntity.ok().build();
    }




    @GetMapping("/admin/all-cars")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CarAdSummaryDto>> getAllCarsForAdmin() {
        return ResponseEntity.ok(carAdService.getAllCarsForAdmin());
    }



    @GetMapping("/admin/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CarAdSummaryDto>> getCarsByStatus(
            @Parameter(
                    required = true,
                    example = "APPROVED",
                    schema = @Schema(implementation = AdStatus.class)
            )
            @PathVariable AdStatus status) {
        List<CarAdSummaryDto> cars = carAdService.getCarsByStatus(status);
        return ResponseEntity.ok(cars);
    }


    @Operation(
            summary = "Sotuvdagilarni olish hamasini  "
    )
    @GetMapping("/active")
    public ResponseEntity<List<CarAdSummaryDto>> getActiveCars() {

        List<CarAdSummaryDto> cars = carAdService.getAllActiveCars();
        return ResponseEntity.ok(cars);
    }



    @Operation(
            summary = "Sotuvdagilarni olish pagenation bilan "
    )
    @GetMapping("/approved")
    public ResponseEntity<Page<CarAdSummaryDto>> getApprovedCars(
            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "5") int size,

            @RequestParam(defaultValue = "price") String sortBy,

            @RequestParam(defaultValue = "desc") String direction) {


        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sortBy)
        );


        Page<CarAdSummaryDto> result = carAdService.getApprovedCars(pageable);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/all/search")
    public ResponseEntity<Page<CarAdSummaryDto>> searchCars(
            // Brand va Model endi List<Long> bo‘ladi
            @Parameter(description = "Brend ID lar (bir nechta: 1,3,5)")
            @RequestParam(required = false) List<Long> brands,

            @Parameter(description = "Model ID lar (bir nechta: 7,8,10)")
            @RequestParam(required = false) List<Long> models,

            @Parameter(description = "Minimal narx", example = "10000000")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Maksimal narx", example = "50000000")
            @RequestParam(required = false) BigDecimal maxPrice,

            @Parameter(description = "Minimal yil", example = "2015")
            @RequestParam(required = false) Integer minYear,

            @Parameter(description = "Maksimal yil", example = "2025")
            @RequestParam(required = false) Integer maxYear,

            @Parameter(description = "Rang (bir nechta:OQ, QORA, KULRANG, KUMUSH, QIZIL, KO_K, JIGARRANG)")
            @RequestParam(required = false) List<String> color,

            @Parameter(description = "Transmission (bir nechta:   AVTOMAT, MEXANIKA, CVT, ROBOTIC)")
            @RequestParam(required = false) List<String> transmission,

            @Parameter(description = "Fuel Type (bir nechta:PETROL, DIESEL, GAS, ELECTRIC, HYBRID )")
            @RequestParam(required = false) List<String> fuelType,

            @Parameter(description = "Sahifa raqami (0 dan)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Sahifadagi soni", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort maydoni (price, year, mileage, createdAt)", example = "createdAt")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Sort tartibi (asc/desc)", example = "desc")
            @RequestParam(required = false) String direction) {


        Sort sort = (sortBy != null && !sortBy.isBlank())
                ? Sort.by(Sort.Direction.fromString(direction != null ? direction : "desc"), sortBy)
                : Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page, size, sort);


        Page<CarAdSummaryDto> result = carAdService.searchApprovedCars(
                brands, models, minPrice, maxPrice, minYear, maxYear,
                color, transmission, fuelType, pageable
        );

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Mashina qidirish")
    @GetMapping("/search")
    public ResponseEntity<Page<CarAdSummaryDto>> searchCars(
            @Parameter(description = "Qidiruv so'zi (brand, model, tavsif)")
            @RequestParam(required = false) String keyword,



            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        Page<CarAdSummaryDto> result;


            result = carAdService.searchByKeyword(keyword.trim(), pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/ads/{status}")
    public ResponseEntity<Page<CarAdSummaryDto>> getUserAdsByStatus(
            @Parameter(description = "User ID si (admin kiritadi)", required = true)
            @PathVariable Long userId,

            @Parameter(
                    description = "Status qiymati (PENDING, APPROVED, SOLD va h.k.)",
                    required = true,
                    example = "APPROVED",
                    schema = @Schema(implementation = AdStatus.class)
            )
            @PathVariable AdStatus status,

            @Parameter(description = "Sahifa raqami (0 dan)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Sahifadagi e'lonlar soni", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort maydoni", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort tartibi", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        // Service chaqiruvi — userId ni parametr sifatida beramiz
        Page<CarAdSummaryDto> userAds = carAdService.getUserAdsByStatus(userId, status, pageable);

        return ResponseEntity.ok(userAds);
    }
}
