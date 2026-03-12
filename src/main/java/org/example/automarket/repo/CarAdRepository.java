package org.example.automarket.repo;

// CarAdRepository.java (filtr va pagination uchun JpaSpecificationExecutor qo'shildi)
import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.User;
import org.example.automarket.entity.enums.AdStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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


    @Query("SELECT c FROM CarAd c WHERE c.status = :status ORDER BY c.createdAt DESC")
    List<CarAd> findAllByStatusOrderByCreatedAtDesc(@Param("status") AdStatus status);

    // Hamma statusdagi e'lonlar, eng yangi birinchi
    List<CarAd> findAllByOrderByCreatedAtDesc();

    Page<CarAd> findBySellerAndStatusOrderByCreatedAtDesc(
            User seller,
            AdStatus status,
            Pageable pageable
    );


    @Query("""
        SELECT c FROM CarAd c 
        WHERE LOWER(c.model.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.model.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY c.createdAt DESC
    """)
    Page<CarAd> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 2. Brand va Model ID lar bo'yicha filtr
    @Query("""
        SELECT c FROM CarAd c 
        WHERE (:brands IS NULL OR c.model.brand.id IN :brands)
          AND (:models IS NULL OR c.model.id IN :models)
        ORDER BY c.createdAt DESC
    """)
    Page<CarAd> searchByBrandsAndModels(
            @Param("brands") List<Long> brands,
            @Param("models") List<Long> models,
            Pageable pageable);

    long countByStatus(AdStatus status);

    @Query("SELECT COUNT(c) FROM CarAd c WHERE c.createdAt >= :start AND c.createdAt <= :end")
    long countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(c) FROM CarAd c WHERE c.createdAt >= :start AND c.createdAt <= :end AND c.status = :status")
    long countByDateRangeAndStatus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("status") AdStatus status);

    @Query("SELECT AVG(c.price) FROM CarAd c WHERE c.status = :status")
    Double findAveragePriceByStatus(@Param("status") AdStatus status);




      @Query("""
          SELECT AVG(c.price)
           FROM CarAd c
           WHERE (:startDate IS NULL OR c.createdAt >= :startDate)
         AND (:endDate IS NULL OR c.createdAt <= :endDate)
          AND c.status = :status
           """)
      Double findAveragePriceByStatusAndDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") AdStatus status
    );

    @Query(value = """
           SELECT 

          COUNT(*) as total_ads,

           COUNT(*) FILTER (WHERE status='APPROVED') as approved,
           COUNT(*) FILTER (WHERE status='PENDING') as pending,
           COUNT(*) FILTER (WHERE status='REJECTED') as rejected,
           COUNT(*) FILTER (WHERE status='SOLD') as sold,

          AVG(price) FILTER (WHERE status='APPROVED') as avg_price,

           COUNT(*) FILTER (WHERE created_at >= CURRENT_DATE) as today_ads,

           COUNT(*) FILTER (WHERE created_at >= CURRENT_DATE - INTERVAL '7 day') as last_week_ads,

             COUNT(*) FILTER (WHERE created_at >= CURRENT_DATE - INTERVAL '30 day') as last_month_ads

           FROM car_ads
          """, nativeQuery = true)
      Object[] getFullAdminStats();
}


