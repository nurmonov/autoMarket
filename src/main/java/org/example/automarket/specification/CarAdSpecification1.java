package org.example.automarket.specification;

import org.example.automarket.entity.CarAd;
import org.example.automarket.entity.enums.AdStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CarAdSpecification1 {

    public static Specification<CarAd> hasStatus(AdStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<CarAd> hasBrand(Long brandId) {
        return (root, query, cb) -> cb.equal(root.get("model").get("brand").get("id"), brandId);
    }

    public static Specification<CarAd> hasModel(Long modelId) {
        return (root, query, cb) -> cb.equal(root.get("model").get("id"), modelId);
    }

    public static Specification<CarAd> priceGreaterThanEqual(BigDecimal price) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<CarAd> priceLessThanEqual(BigDecimal price) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<CarAd> yearGreaterThanEqual(Integer year) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("year"), year);
    }

    public static Specification<CarAd> yearLessThanEqual(Integer year) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("year"), year);
    }

    public static Specification<CarAd> hasColor(String color) {
        return (root, query, cb) -> cb.equal(root.get("color"), color);
    }

    public static Specification<CarAd> hasTransmission(String transmission) {
        return (root, query, cb) -> cb.equal(root.get("transmission"), transmission);
    }

    public static Specification<CarAd> hasFuelType(String fuelType) {
        return (root, query, cb) -> cb.equal(root.get("fuelType"), fuelType);
    }
}
