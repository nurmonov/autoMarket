package org.example.automarket.repo;

// FavoriteRepository.java

import org.example.automarket.entity.Favorite;
import org.example.automarket.entity.FavoriteId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId>, JpaSpecificationExecutor<Favorite> {

    Page<Favorite> findByIdUserId(Long userId, Pageable pageable);  // Updated query method for userId (note the "Id" prefix for embedded)

    boolean existsByIdUserIdAndIdCarAdId(Long userId, Long carAdId);

    void deleteByIdUserIdAndIdCarAdId(Long userId, Long carAdId);

    // 1. Foydalanuvchi ID va CarAd ID bo'yicha mavjudligini tekshirish
    boolean existsByUserIdAndCarAdId(Long userId, Long carAdId);

    // 2. Foydalanuvchi ID va CarAd ID bo'yicha o'chirish
    void deleteByUserIdAndCarAdId(Long userId, Long carAdId);

    // 3. Foydalanuvchining barcha saralangan e'lonlari
    List<Favorite> findByUserId(Long userId);
}