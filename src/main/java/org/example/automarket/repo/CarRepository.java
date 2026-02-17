package org.example.automarket.repo;

import org.example.automarket.entity.Car;
import org.example.automarket.entity.enums.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Car Repository (Filtrlash metodlari bilan)
@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> findAllByBrand_Id(Integer brandId);
    List<Car> findAllByStatus(CarStatus status);
}


