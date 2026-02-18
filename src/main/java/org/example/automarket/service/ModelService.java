package org.example.automarket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.ModelCreateRequest;
import org.example.automarket.dto.ModelResponseDto;
import org.example.automarket.entity.Brand;
import org.example.automarket.entity.Model;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.repo.BrandRepository;
import org.example.automarket.repo.ModelRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelService {

    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final AutoMarketMapper mapper;

    public List<ModelResponseDto> getAllModels() {
        return mapper.toModelResponseDtoList(modelRepository.findAll());
    }

    public List<ModelResponseDto> getModelsByBrand(Long brandId) {
        return mapper.toModelResponseDtoList(modelRepository.findByBrandId(brandId));
    }

    public ModelResponseDto getModelById(Long id) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Model topilmadi"));
        return mapper.toModelResponseDto(model);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ModelResponseDto createModel(ModelCreateRequest request) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand topilmadi"));

        Model model = Model.builder()
                .brand(brand)
                .name(request.getName())
                .build();

        return mapper.toModelResponseDto(modelRepository.save(model));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteModel(Long id) {
        if (!modelRepository.existsById(id)) {
            throw new EntityNotFoundException("Model topilmadi");
        }
        modelRepository.deleteById(id);
    }
}