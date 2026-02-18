package org.example.automarket.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.FavoriteDto;
import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.Favorite;
import org.example.automarket.entity.User;
import org.example.automarket.mapper.AutoMarketMapper;
import org.example.automarket.repo.CarAdRepository;
import org.example.automarket.repo.FavoriteRepository;
import org.example.automarket.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final CarAdRepository carAdRepository;
    private final AutoMarketMapper mapper;
    private final UserRepository userRepository;

    @Transactional
    public void addToFavorites(Long carAdId) {
        User currentUser = getCurrentUser();  // SecurityContext dan
        CarAd carAd = carAdRepository.findById(carAdId)
                .orElseThrow(() -> new EntityNotFoundException("E'lon topilmadi"));

        if (favoriteRepository.existsByUserIdAndCarAdId(currentUser.getId(), carAdId)) {
            throw new IllegalStateException("Bu e'lon allaqachon saralanganlarda");
        }

        Favorite favorite = Favorite.builder()
                .user(currentUser)
                .carAd(carAd)
                .addedAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFromFavorites(Long carAdId) {
        User currentUser = getCurrentUser();
        favoriteRepository.deleteByUserIdAndCarAdId(currentUser.getId(), carAdId);
    }

    @Transactional(readOnly = true)
    public List<FavoriteDto> getMyFavorites() {
        User currentUser = getCurrentUser();
        return mapper.toFavoriteDtoList(
                favoriteRepository.findByUserId(currentUser.getId())
        );
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long carAdId) {
        User currentUser = getCurrentUser();
        return favoriteRepository.existsByUserIdAndCarAdId(currentUser.getId(), carAdId);
    }

    private User getCurrentUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User topilmadi"));
    }
}
