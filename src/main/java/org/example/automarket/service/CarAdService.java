package org.example.automarket.service;


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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarAdService {

    private final CarAdRepository carAdRepository;
    private final ModelRepository modelRepository;
//    private final CarImageService carImageService;
    private final AutoMarketMapper mapper;
    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @Transactional
    public CarAdDetailDto createCarAd(CarAdCreateRequest request, List<MultipartFile> images) {
        User currentUser = getCurrentUser();

        Model model = modelRepository.findById(request.getModelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Model topilmadi"));

        CarAd carAd = mapper.toCarAd(request);
        carAd.setSeller(currentUser);
        carAd.setModel(model);
        carAd.setStatus(AdStatus.PENDING);
        carAd.setCreatedAt(LocalDateTime.now());

        carAd = carAdRepository.save(carAd);

//        if (!images.isEmpty()) {
//            carImageService.uploadImages(carAd.getId(), images);
//        }

        return mapper.toCarAdDetailDto(carAd);
    }

    @Transactional(readOnly = true)
    public Page<CarAdSummaryDto> searchCarAds(CarAdSearchRequest searchRequest, Pageable pageable) {
        Specification<CarAd> spec = CarAdSpecification.search(searchRequest)
                .and((root, query, cb) -> cb.equal(root.get("status"), AdStatus.APPROVED));  // faqat tasdiqlanganlar

        Page<CarAd> page = carAdRepository.findAll(spec, pageable);
        return page.map(this::mapToSummaryWithFavorite);
    }

    @Transactional(readOnly = true)
    public CarAdDetailDto getCarAdDetail(Long id) {
        CarAd carAd = carAdRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "E'lon topilmadi"));

        CarAdDetailDto dto = mapper.toCarAdDetailDto(carAd);
        dto.setFavorite(favoriteService.isFavorite(id));  // joriy user uchun
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

//        if (!newImages.isEmpty()) {
//            carImageService.uploadImages(id, newImages);
//        }

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
            // rejectReason ni saqlash uchun qo'shimcha field qo'shsa bo'ladi
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
}