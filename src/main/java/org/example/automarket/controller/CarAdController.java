package org.example.automarket.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.CarAdCreateRequest;
import org.example.automarket.dto.CarAdSearchRequest;
import org.example.automarket.dto.CarAdUpdateRequest;
import org.example.automarket.dto.ModerateCarAdRequest;
import org.example.automarket.dto.CarAdDetailDto;
import org.example.automarket.dto.CarAdSummaryDto;
import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.enums.AdStatus;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.repo.CarAdRepository;
import org.example.automarket.service.CarAdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        CarAdDetailDto dto = carAdService.createCarAd(request, List.of());  // rasmlarsiz yaratamiz
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CarAdSummaryDto>> search(
            @ModelAttribute CarAdSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        return ResponseEntity.ok(carAdService.searchCarAds(searchRequest, pageable));
    }

    @GetMapping("/{id}")
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


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tanlangan statusdagi mashinalar ro'yxati"),
            @ApiResponse(responseCode = "400", description = "Noto'g'ri status kiritildi"),
            @ApiResponse(responseCode = "403", description = "Faqat ADMIN roli uchun")
    })
    @GetMapping("/by-status/{status}")
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
            summary = "Hozirda sotuvdagi barcha mashinalarni olish"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sotuvdagi mashinalar ro'yxati muvaffaqiyatli qaytarildi"),
            @ApiResponse(responseCode = "401", description = "Autentifikatsiya talab qilinadi")
    })
    @GetMapping("/active")
    public ResponseEntity<List<CarAdSummaryDto>> getActiveCars() {

        List<CarAdSummaryDto> cars = carAdService.getAllActiveCars();

        return ResponseEntity.ok(cars);
    }
}
