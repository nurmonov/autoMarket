package org.example.automarket.repo;

// CarAdRepository.java (filtr va pagination uchun JpaSpecificationExecutor qo'shildi)
import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.enums.AdStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarAdRepository extends JpaRepository<CarAd, Long>, JpaSpecificationExecutor<CarAd> {

    // APPROVED e'lonlar pagination bilan (masalan, homepage uchun)
    Page<CarAd> findAllByStatus(AdStatus status, Pageable pageable);

    // Sotuvchining e'lonlari
    Page<CarAd> findBySellerId(Long sellerId, Pageable pageable);

    // Featured e'lonlar
    Page<CarAd> findByIsFeaturedTrueAndStatus(AdStatus status, Pageable pageable);

    // Admin uchun PENDING e'lonlar
    Page<CarAd> findByStatus(AdStatus status, Pageable pageable);  // PENDING ni pagination bilan olish uchun

    long countBySellerId(Long sellerId);
    long countBySellerIdAndStatus(Long sellerId, AdStatus status);


    // Joriy sotuvchining muayyan statusdagi e'lonlari (masalan: SOLD, PENDING)
    List<CarAd> findBySellerIdAndStatus(Long sellerId, AdStatus status);

    // Agar pagination ham kerak bo'lsa (masalan, sotilgan e'lonlar ko'p bo'lsa)
    Page<CarAd> findBySellerIdAndStatus(Long sellerId, AdStatus status, Pageable pageable);


}


