package org.example.automarket.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.AdminStatsDto;
import org.example.automarket.dto.AdminStatsResponse;
import org.example.automarket.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin")
public class AdminController {

    private final AdminService adminService;


    @Operation(summary = "Admin uchun umumiy statistika")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsDto> getAdminStats() {
        AdminStatsDto stats = adminService.getAdminStats();
        return ResponseEntity.ok(stats);
    }
//    @GetMapping("/admin/stats")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<AdminStatsResponse> getAdminStatsFull(
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) Integer month) {
//
//
//        AdminStatsResponse stats = adminService.getAdminStatsFull(year, month);
//
//        return ResponseEntity.ok(stats);
//    }
//
//    @GetMapping("/full")
//    public ResponseEntity<AdminStatsDto> getAdminStatsJJ() {
//
//        AdminStatsDto stats = adminService.getFullStats();
//
//        return ResponseEntity.ok(stats);
//    }
}
