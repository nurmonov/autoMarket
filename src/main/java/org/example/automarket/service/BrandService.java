package org.example.automarket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.BrandCreateRequest;
import org.example.automarket.dto.BrandResponseDto;
import org.example.automarket.dto.BrandUpdateRequest;
import org.example.automarket.entity.Brand;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.repo.BrandRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final AutoMarketMapper mapper;

    public List<BrandResponseDto> getAllBrands() {
        return mapper.toBrandResponseDtoList(brandRepository.findAll());
    }

    public BrandResponseDto getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand topilmadi: " + id));
        return mapper.toBrandResponseDto(brand);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public BrandResponseDto createBrand(BrandCreateRequest request) {
        Brand brand = Brand.builder()
                .name(request.getName())
                .logoUrl(request.getLogoUrl())
                .build();
        return mapper.toBrandResponseDto(brandRepository.save(brand));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public BrandResponseDto updateBrand(Long id, BrandUpdateRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand topilmadi"));

        if (request.getName() != null) brand.setName(request.getName());
        if (request.getLogoUrl() != null) brand.setLogoUrl(request.getLogoUrl());

        return mapper.toBrandResponseDto(brandRepository.save(brand));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new EntityNotFoundException("Brand topilmadi");
        }
        brandRepository.deleteById(id);
    }
}
