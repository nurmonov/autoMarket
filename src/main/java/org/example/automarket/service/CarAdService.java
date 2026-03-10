package org.example.automarket.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.*;
import org.example.automarket.entity.enums.AdStatus;
import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.Model;
import org.example.automarket.entity.User;
import org.example.automarket.entity.enums.Role;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.repo.CarAdRepository;
import org.example.automarket.repo.ModelRepository;
import org.example.automarket.repo.UserRepository;
import org.example.automarket.specification.CarAdSpecification;
import org.example.automarket.specification.CarAdSpecification1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarAdService {

    private final CarAdRepository carAdRepository;
    private final ModelRepository modelRepository;
    private final CarImageService carImageService;
    private final AutoMarketMapper mapper;
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @Transactional
    public CarAdDetailDto createCarAd(CarAdCreateRequest request, List<MultipartFile> images) {
        User currentUser = getCurrentUser();

        Model model = modelRepository.findById(request.getModelId())
                .orElseThrow(() -> new EntityNotFoundException("Model topilmadi"));

        CarAd carAd = mapper.toCarAd(request);
        carAd.setSeller(currentUser);
        carAd.setModel(model);
        carAd.setStatus(AdStatus.PENDING);
        carAd.setCreatedAt(LocalDateTime.now());

        carAd = carAdRepository.save(carAd);

        // Rasmlar bo'lsa yuklaymiz (hozircha bo'sh jo'natiladi)
        if (!images.isEmpty()) {
            carImageService.uploadImages(carAd.getId(), images);
        }

        return mapper.toCarAdDetailDto(carAd);
    }



    @Transactional(readOnly = true)
    public CarAdDetailDto getCarAdDetail(Long id) {
        CarAd carAd = carAdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "E'lon topilmadi"));

        CarAdDetailDto dto = mapper.toCarAdDetailDto(carAd);
       // dto.setFavorite(favoriteService.isFavorite(id));  // joriy user uchun
        return dto;
    }

    @Transactional
    public CarAdDetailDto updateCarAd(Long id, CarAdUpdateRequest request, List<MultipartFile> newImages) {
        CarAd carAd = carAdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "E'lon topilmadi"));

        if (!isOwnerOrAdmin(carAd)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Siz bu e'lonni tahrirlay olmaysiz");
        }

        mapper.updateCarAdFromRequest(request, carAd);
        carAd.setUpdatedAt(LocalDateTime.now());


        return mapper.toCarAdDetailDto(carAdRepository.save(carAd));
    }

    @Transactional
    public void deleteCarAd(Long id) {
        CarAd carAd = carAdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "E'lon topilmadi"));

        if (!isOwnerOrAdmin(carAd)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Siz bu e'lonni o'chira olmaysiz");
        }

        carAdRepository.delete(carAd);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void moderateCarAd(Long id, ModerateCarAdRequest request) {
        CarAd carAd = carAdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "E'lon topilmadi"));

        if (request.isApprove()) {
            carAd.setStatus(AdStatus.APPROVED);
            carAd.setApprovedAt(LocalDateTime.now());
        } else {
            carAd.setStatus(AdStatus.REJECTED);

        }

        carAdRepository.save(carAd);
    }

    private boolean isOwnerOrAdmin(CarAd carAd) {
        User currentUser = getCurrentUser();
        return carAd.getSeller().getId().equals(currentUser.getId()) || currentUser.getRole() == Role.ADMIN;
    }

    private User getCurrentUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private CarAdSummaryDto mapToSummaryWithFavorite(CarAd carAd) {
        CarAdSummaryDto dto = mapper.toCarAdSummaryDto(carAd);
        dto.setFavorite(favoriteService.isFavorite(carAd.getId()));
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<CarAdSummaryDto> searchCarAds(CarAdSearchRequest request, Pageable pageable) {
        Specification<CarAd> spec = CarAdSpecification.search(request)
                .and((root, query, cb) -> cb.equal(root.get("status"), AdStatus.APPROVED));

        Page<CarAd> page = carAdRepository.findAll(spec, pageable);

        return page.map(carAd -> {
            CarAdSummaryDto dto = mapper.toCarAdSummaryDto(carAd);

            return dto;
        });
    }




    @Transactional(readOnly = true)
    public List<CarAdSummaryDto> getAllApprovedCars() {

        List<CarAd> cars = carAdRepository.findAllByStatusOrderByCreatedAtDesc(AdStatus.APPROVED);

        
        return cars.stream()
                .map(mapper::toCarAdSummaryDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<CarAdSummaryDto> getAllCarsForAdmin() {
        List<CarAd> cars = carAdRepository.findAllByOrderByCreatedAtDesc();
        return cars.stream()
                .map(mapper::toCarAdSummaryDto)
                .collect(Collectors.toList());
    }

    public List<CarAdSummaryDto> getCarsByStatus(AdStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status qiymati bo'sh bo'lishi mumkin emas");
        }

        List<CarAd> cars = carAdRepository.findAllByStatusOrderByCreatedAtDesc(status);

        return cars.stream()
                .map(mapper::toCarAdSummaryDto)
                .collect(Collectors.toList());
    }


    public List<CarAdSummaryDto> getAllActiveCars() {
        List<CarAd> cars = carAdRepository.findAllByStatusOrderByCreatedAtDesc(AdStatus.APPROVED);

        return cars.stream()
                .map(mapper::toCarAdSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CarAdSummaryDto> getApprovedCars(Pageable pageable) {

        Page<CarAd> page = carAdRepository.findAllByStatus(AdStatus.APPROVED, pageable);

        return page.map(carAd -> mapper.toCarAdSummaryDto(carAd));
    }

//    @Transactional(readOnly = true)
//    public Page<CarAdSummaryDto> searchApprovedCars(
//            Long brandId, Long modelId, BigDecimal minPrice, BigDecimal maxPrice,
//            Integer minYear, Integer maxYear,
//            String color, String transmission, String fuelType,
//            Pageable pageable) {
//
//
//        Specification<CarAd> spec = Specification.where(CarAdSpecification1.hasStatus(AdStatus.APPROVED));
//
//        if (brandId != null) {
//            spec = spec.and(CarAdSpecification1.hasBrand(brandId));
//        }
//        if (modelId != null) {
//            spec = spec.and(CarAdSpecification1.hasModel(modelId));
//        }
//        if (minPrice != null) {
//            spec = spec.and(CarAdSpecification1.priceGreaterThanEqual(minPrice));
//        }
//        if (maxPrice != null) {
//            spec = spec.and(CarAdSpecification1.priceLessThanEqual(maxPrice));
//        }
//        if (minYear != null) {
//            spec = spec.and(CarAdSpecification1.yearGreaterThanEqual(minYear));
//        }
//        if (maxYear != null) {
//            spec = spec.and(CarAdSpecification1.yearLessThanEqual(maxYear));
//        }
//        if (color != null && !color.isBlank()) {
//            spec = spec.and(CarAdSpecification1.hasColor(color));
//        }
//        if (transmission != null && !transmission.isBlank()) {
//            spec = spec.and(CarAdSpecification1.hasTransmission(transmission));
//        }
//        if (fuelType != null && !fuelType.isBlank()) {
//            spec = spec.and(CarAdSpecification1.hasFuelType(fuelType));
//        }
//
//        Page<CarAd> page = carAdRepository.findAll(spec, pageable);
//
//        return page.map(mapper::toCarAdSummaryDto);
//    }

    public Page<CarAdSummaryDto> searchByKeyword(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        Page<CarAd> page = carAdRepository.searchByKeyword(keyword.trim(), pageable);
        return page.map(mapper::toCarAdSummaryDto);
    }


    public Page<CarAdSummaryDto> searchByBrandsAndModels(
            List<Long> brands, List<Long> models, Pageable pageable) {

        Page<CarAd> page = carAdRepository.searchByBrandsAndModels(brands, models, pageable);
        return page.map(mapper::toCarAdSummaryDto);
    }

    @Transactional(readOnly = true)
    public Page<CarAdSummaryDto> searchApprovedCars(
            List<Long> brands,
            List<Long> models,
            BigDecimal minPrice, BigDecimal maxPrice,
            Integer minYear, Integer maxYear,
            List<String> colors,
            List<String> transmissions,
            List<String> fuelTypes,
            Pageable pageable) {


        Specification<CarAd> spec = Specification.where(
                CarAdSpecification1.hasStatus(AdStatus.APPROVED)
        );


        if (brands != null && !brands.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("model").get("brand").get("id").in(brands));
        }


        if (models != null && !models.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("model").get("id").in(models));
        }

        if (minPrice != null) {
            spec = spec.and(CarAdSpecification1.priceGreaterThanEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(CarAdSpecification1.priceLessThanEqual(maxPrice));
        }
        if (minYear != null) {
            spec = spec.and(CarAdSpecification1.yearGreaterThanEqual(minYear));
        }
        if (maxYear != null) {
            spec = spec.and(CarAdSpecification1.yearLessThanEqual(maxYear));
        }


        if (colors != null && !colors.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("color").in(colors));
        }

        if (transmissions != null && !transmissions.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("transmission").in(transmissions));
        }


        if (fuelTypes != null && !fuelTypes.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("fuelType").in(fuelTypes));
        }

        Page<CarAd> page = carAdRepository.findAll(spec, pageable);
        return page.map(mapper::toCarAdSummaryDto);
    }

    @Transactional(readOnly = true)
    public Page<CarAdSummaryDto> getUserAdsByStatus(Long userId, AdStatus status, Pageable pageable) {
        if (status == null) {
            throw new IllegalArgumentException("Status qiymati bo'sh bo'lishi mumkin emas");
        }


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User topilmadi: ID = " + userId));


        Page<CarAd> page = carAdRepository.findBySellerAndStatusOrderByCreatedAtDesc(
                user,
                status,
                pageable
        );

        return page.map(mapper::toCarAdSummaryDto);
    }

}