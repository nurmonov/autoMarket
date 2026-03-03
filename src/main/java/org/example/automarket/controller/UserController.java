package org.example.automarket.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AutoMarketMapper mapper;

    @Operation(
            summary = "Joriy foydalanuvchi malumotlarini olish (GET ME)")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        UserResponseDto userDto = userService.getMe();
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Parol muvaffaqiyatli o'zgartirildi");
    }

   @Operation(
           summary = "Joriy foydalanuvchining statistikasi ")
    @GetMapping("/me/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserStatsDto> getMyStats() {
        UserStatsDto stats = userService.getMyStats();
        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "Joriy foydalanuvchining barcha elonlari (pagination bilan) "
    )
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

    @Operation(
            summary = "test uchun  api  api/users/me/ads/{status} umumlashtirgan "
    )
    @GetMapping("/me/ads/solding")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CarAdSummaryDto>> getMySoldAds() {
        List<CarAd> soldAds = userService.getMyCarAdsByStatus(AdStatus.SOLD);
        List<CarAdSummaryDto> dtos = soldAds.stream()
                .map(mapper::toCarAdSummaryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Operation(
            summary = "test uchun  api  api/users/me/ads/{status} umumlashtirgan "
    )
    @GetMapping("/me/ads/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CarAdSummaryDto>> getMyPendingAds() {
        List<CarAd> pendingAds = userService.getMyCarAdsByStatus(AdStatus.PENDING);
        List<CarAdSummaryDto> dtos = pendingAds.stream()
                .map(mapper::toCarAdSummaryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

 @Operation(
         summary = "Admin uchun: barcha foydalanuvchilar royxati (faqat ADMIN)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Admin uchun: bitta foydalanuvchi malumotlarini olish (ID bo'yicha)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }
    @Operation(
            summary = "test uchun  api  api/users/me/ads/{status} umumlashtirgan "
    )
    @GetMapping("/me/ads/sold")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CarAdSummaryDto>> getMySoldAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CarAdSummaryDto> soldAds = userService.getMySoldAdsPaged(page, size);
        return ResponseEntity.ok(soldAds);
    }

    @Operation(
            summary = "statuslar buyicha malumotlarni olish"
    )
    @GetMapping("/me/ads/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CarAdSummaryDto>> getMyAdsByStatus(
            @Parameter(
                    description = "Status qiymati (PENDING, APPROVED, SOLD)",
                    required = true,
                    example = "APPROVED",
                    schema = @Schema(implementation = AdStatus.class)
            )
            @PathVariable AdStatus status,

            @Parameter(description = "Sahifa raqami (0 dan)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Sahifadagi soni", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort maydoni", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort tartibi", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        Page<CarAdSummaryDto> myAds = userService.getMyAdsByStatus(status, pageable);

        return ResponseEntity.ok(myAds);
    }

}
