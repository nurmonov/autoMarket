package org.example.automarket.repo;

// ModelRepository.java
import org.example.automarket.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long>, JpaSpecificationExecutor<Model> {

    @Query("SELECT m FROM Model m WHERE m.brand.id = :brandId")
    List<Model> findByBrandId(Long brandId);
}
