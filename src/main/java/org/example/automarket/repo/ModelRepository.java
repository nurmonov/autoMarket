package org.example.automarket.repo;

import org.example.automarket.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {
    List<Model> findAllByBrandId(Integer brandId);
}
