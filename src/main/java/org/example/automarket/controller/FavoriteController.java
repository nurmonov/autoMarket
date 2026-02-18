package org.example.automarket.controller;

import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.FavoriteDto;
import org.example.automarket.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{carAdId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long carAdId) {
        favoriteService.addToFavorites(carAdId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{carAdId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long carAdId) {
        favoriteService.removeFromFavorites(carAdId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoriteDto>> getMyFavorites() {
        return ResponseEntity.ok(favoriteService.getMyFavorites());
    }

    @GetMapping("/check/{carAdId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long carAdId) {
        return ResponseEntity.ok(favoriteService.isFavorite(carAdId));
    }
}
