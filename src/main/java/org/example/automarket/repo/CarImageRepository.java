package org.example.automarket.repo;

// CarImageRepository.java (rasmlar uchun, agar kerak bo'lsa, lekin odatda CarAd bilan cascade qilinadi)
import org.example.automarket.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage, Long> {
    List<CarImage> findByCarAdIdOrderByOrderIndexAsc(Long carAdId);


    Optional<CarImage> findByCarAdIdAndIsMainTrue(Long carAdId);

    void deleteByCarAdId(Long carAdId);

}
