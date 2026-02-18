package org.example.automarket.service;


import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.*;
import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.enums.AdStatus;
import org.example.automarket.entity.enums.Role;
import org.example.automarket.entity.User;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.repo.CarAdRepository;
import org.example.automarket.repo.UserRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CarAdRepository carAdRepository;
    private final PasswordEncoder passwordEncoder;
    private final AutoMarketMapper mapper;

    public User register(UserRegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Bu telefon raqami allaqachon ro'yxatdan o'tgan");
        }

        User user = mapper.toUser(request);  // Agar mapperda bor bo'lsa
        // yoki qo'lda:
        // User user = new User();
        // user.setPhone(request.getPhone());
        // user.setEmail(request.getEmail());
        // user.setFullName(request.getFullName());
        // user.setRegion(request.getRegion());
        // user.setCity(request.getCity());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);  // default
        user.setActive(true);

        return userRepository.save(user);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Eski parol noto'g'ri");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserResponseDto getMe() {
        User user = getCurrentUser();
        return mapper.toUserResponseDto(user);
    }

    public UserStatsDto getMyStats() {
        User user = getCurrentUser();
        long totalAds = carAdRepository.countBySellerId(user.getId());
        long soldAds = carAdRepository.countBySellerIdAndStatus(user.getId(), AdStatus.SOLD);
        long pendingAds = carAdRepository.countBySellerIdAndStatus(user.getId(), AdStatus.PENDING);

        return UserStatsDto.builder()
                .totalAds(totalAds)
                .soldAds(soldAds)
                .pendingAds(pendingAds)
                .build();
    }

    // UserService.java ichiga qo'shing

    @Transactional(readOnly = true)
    public Page<CarAd> getMyCarAds(Pageable pageable) {
        User currentUser = getCurrentUser();
        return carAdRepository.findBySellerId(currentUser.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public List<CarAd> getMyCarAdsByStatus(AdStatus status) {
        User currentUser = getCurrentUser();
        return carAdRepository.findBySellerIdAndStatus(currentUser.getId(), status);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi"));
        return mapper.toUserResponseDto(user);
    }


    @Transactional(readOnly = true)
    public Page<CarAdSummaryDto> getMySoldAdsPaged(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<CarAd> pageResult = carAdRepository.findBySellerIdAndStatus(
                currentUser.getId(),
                AdStatus.SOLD,
                pageable
        );

        return pageResult.map(mapper::toCarAdSummaryDto);
    }

    private User getCurrentUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi"));
    }
}
