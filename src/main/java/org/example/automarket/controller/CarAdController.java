package org.example.automarket.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.*;
import org.example.automarket.entity.enums.AdStatus;
import org.example.automarket.service.CarAdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarAdController {

    private final CarAdService carAdService;

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

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<CarAdDetailDto> patch(
            @PathVariable Long id,
            @Valid @RequestPart("request") CarAdUpdateRequest request
           ) {

        CarAdDetailDto dto = carAdService.updateCarAd(id, request);
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

            @RequestParam(defaultValue = "12") int size,

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


    @Operation(summary = "Mashina qidirish")
    @GetMapping("/search")
    public ResponseEntity<Page<CarAdSummaryDto>> searchCars(

            @Parameter(description = "Qidiruv so'zi")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Brand IDs")
            @RequestParam(required = false) List<Long> brands,

            @Parameter(description = "Model IDs")
            @RequestParam(required = false) List<Long> models,

            @Parameter(description = "Minimal narx")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Maksimal narx")
            @RequestParam(required = false) BigDecimal maxPrice,

            @Parameter(description = "Minimal yil")
            @RequestParam(required = false) Integer minYear,

            @Parameter(description = "Maksimal yil")
            @RequestParam(required = false) Integer maxYear,

            @Parameter(description = "Rang")
            @RequestParam(required = false) List<String> color,

            @Parameter(description = "Transmission")
            @RequestParam(required = false) List<String> transmission,

            @Parameter(description = "Fuel type")
            @RequestParam(required = false) List<String> fuelType,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        Page<CarAdSummaryDto> result;

        if (keyword != null && !keyword.trim().isEmpty()) {

            result = carAdService.searchByKeyword(keyword.trim(), pageable);

        } else {

            result = carAdService.searchApprovedCars(
                    brands,
                    models,
                    minPrice,
                    maxPrice,
                    minYear,
                    maxYear,
                    color,
                    transmission,
                    fuelType,
                    pageable
            );
        }

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

            @Parameter(description = "Sahifadagi e'lonlar soni", example = "6")
            @RequestParam(defaultValue = "6") int size,

            @Parameter(description = "Sort maydoni", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort tartibi", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        Page<CarAdSummaryDto> userAds = carAdService.getUserAdsByStatus(userId, status, pageable);

        return ResponseEntity.ok(userAds);
    }


}
