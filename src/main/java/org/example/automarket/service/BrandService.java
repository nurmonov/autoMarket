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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final AutoMarketMapper mapper;
    private final FileStorageService fileStorageService;

    public List<BrandResponseDto> getAllBrands() {
        return mapper.toBrandResponseDtoList(brandRepository.findAll());
    }

    public BrandResponseDto getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand topilmadi: " + id));
        return mapper.toBrandResponseDto(brand);
    }

    @Transactional
    public BrandResponseDto createBrand(String name, MultipartFile logoFile) {
        // 1. Unique nomni tekshirish (DB xatosi chiqmasligi uchun)
        if (brandRepository.existsByName(name)) {
            throw new IllegalArgumentException("Bunday nomdagi brend allaqachon mavjud: " + name);
        }

        // 2. Yangi brand yaratish
        Brand brand = Brand.builder()
                .name(name)
                .build();

        // 3. Saqlash (ID generatsiya qilinadi)
        brand = brandRepository.save(brand);

        // 4. Logo yuklash (agar yuborilgan bo‘lsa)
        if (logoFile != null && !logoFile.isEmpty()) {
            // Fayl hajmi cheklash (masalan 5MB)
            if (logoFile.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Logo hajmi 5MB dan oshmasligi kerak");
            }

            // Fayl turini tekshirish (faqat rasm)
            String contentType = logoFile.getContentType();
            if (contentType == null || !contentType.startsWith("image")) {
                throw new IllegalArgumentException("Faqat rasm fayllari yuklanishi mumkin (jpg, png, gif)");
            }

            // Logo saqlash
            String subFolder = "brands/" + brand.getId();
            String logoUrl = fileStorageService.store(logoFile, brand.getId(), FileStorageService.EntityType.BRANDS);

            // Logo URL ni set qilish
            brand.setLogoUrl(logoUrl);

            // Yangi URL bilan qayta saqlash
            brand = brandRepository.save(brand);
        }

        // 5. DTO qaytarish
        return mapper.toBrandResponseDto(brand);
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

    @Transactional
    public BrandResponseDto updateBrandLogo(Long brandId, MultipartFile logoFile) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("Brend topilmadi: " + brandId));

        if (logoFile == null || logoFile.isEmpty()) {
            throw new IllegalArgumentException("Logo fayli yuklanmadi");
        }

        // Eski logo bor bo‘lsa o‘chirish
        if (brand.getLogoUrl() != null) {
            fileStorageService.delete(brand.getLogoUrl());
        }

        // Yangi logo saqlash (universal metod bilan)
        String logoUrl = fileStorageService.store(logoFile, brandId, FileStorageService.EntityType.BRANDS);

        brand.setLogoUrl(logoUrl);
        brand = brandRepository.save(brand);

        return mapper.toBrandResponseDto(brand);
    }
}
