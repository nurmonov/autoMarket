package org.example.automarket.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.CarAdCreateRequest;
import org.example.automarket.dto.CarAdSearchRequest;
import org.example.automarket.dto.CarAdUpdateRequest;
import org.example.automarket.dto.ModerateCarAdRequest;
import org.example.automarket.dto.CarAdDetailDto;
import org.example.automarket.dto.CarAdSummaryDto;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarAdController {

    private final CarAdService carAdService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<CarAdDetailDto> create(
            @Valid @RequestPart("request") CarAdCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        CarAdDetailDto dto = carAdService.createCarAd(request, images);
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
}
