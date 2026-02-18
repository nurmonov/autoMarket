package org.example.automarket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.ModelCreateRequest;
import org.example.automarket.dto.ModelResponseDto;
import org.example.automarket.service.ModelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;

    @GetMapping
    public ResponseEntity<List<ModelResponseDto>> getAll() {
        return ResponseEntity.ok(modelService.getAllModels());
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ModelResponseDto>> getByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(modelService.getModelsByBrand(brandId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(modelService.getModelById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModelResponseDto> create(@Valid @RequestBody ModelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modelService.createModel(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        modelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }
}
