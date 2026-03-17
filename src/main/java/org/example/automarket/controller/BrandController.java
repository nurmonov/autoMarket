package org.example.automarket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.BrandResponseDto;
import org.example.automarket.dto.BrandUpdateRequest;
import org.example.automarket.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandResponseDto>> getAll() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponseDto> createBrand(
            @RequestParam String name,
            @RequestPart(value = "images", required = false) MultipartFile images) {

        // Bo'shliklarni olib tashlab, tekshirish
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Brend nomi bo'sh bo'lishi mumkin emas");
        }

        BrandResponseDto dto = brandService.createBrand(name.trim(), images);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponseDto> update(@PathVariable Long id, @Valid @RequestBody BrandUpdateRequest request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/{brandId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponseDto> updateBrandLogo(
            @PathVariable Long brandId,
            @RequestParam("logo") MultipartFile logoFile) {

        // Service chaqiruvi — butun logika shu yerda
        BrandResponseDto updatedBrand = brandService.updateBrandLogo(brandId, logoFile);

        return ResponseEntity.ok(updatedBrand);
    }
}
