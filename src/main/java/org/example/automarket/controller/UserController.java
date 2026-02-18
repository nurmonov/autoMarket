package org.example.automarket.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.*;
import org.example.automarket.entity.enums.AdStatus;
import org.example.automarket.entity.CarAd;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AutoMarketMapper mapper;

    // 1. Joriy foydalanuvchi ma'lumotlarini olish (GET ME)
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        UserResponseDto userDto = userService.getMe();
        return ResponseEntity.ok(userDto);
    }

    // 2. Parolni o'zgartirish
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Parol muvaffaqiyatli o'zgartirildi");
    }

    // 3. Joriy foydalanuvchining statistikasi (e'lonlar soni, sotilganlar va h.k.)
    @GetMapping("/me/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserStatsDto> getMyStats() {
        UserStatsDto stats = userService.getMyStats();
        return ResponseEntity.ok(stats);
    }

    // 4. Joriy foydalanuvchining barcha e'lonlari (pagination bilan)
    @GetMapping("/me/ads")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CarAdSummaryDto>> getMyAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        Page<CarAd> adsPage = userService.getMyCarAds(pageable);
        Page<CarAdSummaryDto> dtoPage = adsPage.map(mapper::toCarAdSummaryDto);

        return ResponseEntity.ok(dtoPage);
    }

    // 5. Joriy foydalanuvchining faqat sotilgan e'lonlari
    @GetMapping("/me/ads/solding")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CarAdSummaryDto>> getMySoldAds() {
        List<CarAd> soldAds = userService.getMyCarAdsByStatus(AdStatus.SOLD);
        List<CarAdSummaryDto> dtos = soldAds.stream()
                .map(mapper::toCarAdSummaryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // 6. Joriy foydalanuvchining moderatsiyada kutayotgan e'lonlari
    @GetMapping("/me/ads/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CarAdSummaryDto>> getMyPendingAds() {
        List<CarAd> pendingAds = userService.getMyCarAdsByStatus(AdStatus.PENDING);
        List<CarAdSummaryDto> dtos = pendingAds.stream()
                .map(mapper::toCarAdSummaryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // 7. Admin uchun: barcha foydalanuvchilar ro'yxati (faqat ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 8. Admin uchun: bitta foydalanuvchi ma'lumotlarini olish (ID bo'yicha)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/me/ads/sold")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CarAdSummaryDto>> getMySoldAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CarAdSummaryDto> soldAds = userService.getMySoldAdsPaged(page, size);
        return ResponseEntity.ok(soldAds);
    }

}
