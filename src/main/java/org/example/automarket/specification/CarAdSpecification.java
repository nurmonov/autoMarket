package org.example.automarket.specification;

import org.example.automarket.dto.CarAdSearchRequest;
import org.example.automarket.entity.enums.AdStatus;
import org.example.automarket.entity.CarAd;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CarAdSpecification {

    public static Specification<CarAd> search(CarAdSearchRequest request) {
        return Specification.where(statusIsApproved())  // faqat tasdiqlanganlar
                .and(hasBrand(request.getBrandId()))
                .and(hasModel(request.getModelId()))
                .and(yearBetween(request.getMinYear(), request.getMaxYear()))
                .and(priceBetween(request.getMinPrice(), request.getMaxPrice(),request.getMaxPrice()))
                .and(hasColor(request.getColor()))
                .and(hasTransmission(request.getTransmission()))
                .and(hasFuelType(request.getFuelType()))
                .and(hasBodyType(request.getBodyType()))
                .and(hasRegion(request.getRegion()));
        // .and(mileageBetween(...)) va boshqa filtrlar qo'shsa bo'ladi
    }

    // Faqat tasdiqlangan e'lonlarni ko'rsatish (ko'pincha kerak)
    private static Specification<CarAd> statusIsApproved() {
        return (root, query, cb) -> cb.equal(root.get("status"), AdStatus.APPROVED);
    }

    private static Specification<CarAd> hasBrand(Long brandId) {
        return (root, query, cb) ->
                brandId == null ? cb.conjunction() :
                        cb.equal(root.get("model").get("brand").get("id"), brandId);
    }

    private static Specification<CarAd> hasModel(Long modelId) {
        return (root, query, cb) ->
                modelId == null ? cb.conjunction() :
                        cb.equal(root.get("model").get("id"), modelId);
    }

    private static Specification<CarAd> yearBetween(Integer minYear, Integer maxYear) {
        return (root, query, cb) -> {
            if (minYear == null && maxYear == null) return cb.conjunction();
            if (minYear == null) return cb.lessThanOrEqualTo(root.get("year"), maxYear);
            if (maxYear == null) return cb.greaterThanOrEqualTo(root.get("year"), minYear);
            return cb.between(root.get("year"), minYear, maxYear);
        };
    }

    private static Specification<CarAd> priceBetween(BigDecimal minPrice, BigDecimal maxPrice,BigDecimal maxYear) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return cb.conjunction();
            if (minPrice == null) return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxYear == null) return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    private static Specification<CarAd> hasColor(String color) {
        return (root, query, cb) ->
                color == null || color.isBlank() ? cb.conjunction() :
                        cb.equal(root.get("color"), color.toUpperCase());
    }

    private static Specification<CarAd> hasTransmission(String transmission) {
        return (root, query, cb) ->
                transmission == null || transmission.isBlank() ? cb.conjunction() :
                        cb.equal(root.get("transmission"), transmission.toUpperCase());
    }

    private static Specification<CarAd> hasFuelType(String fuelType) {
        return (root, query, cb) ->
                fuelType == null || fuelType.isBlank() ? cb.conjunction() :
                        cb.equal(root.get("fuelType"), fuelType.toUpperCase());
    }

    private static Specification<CarAd> hasBodyType(String bodyType) {
        return (root, query, cb) ->
                bodyType == null || bodyType.isBlank() ? cb.conjunction() :
                        cb.equal(root.get("bodyType"), bodyType.toUpperCase());
    }

    private static Specification<CarAd> hasRegion(String region) {
        return (root, query, cb) ->
                region == null || region.isBlank() ? cb.conjunction() :
                        cb.equal(root.get("seller").get("region"), region);
    }

    // Qo'shimcha filtrlar misoli (masalan, km oralig'i)
    /*
    private static Specification<CarAd> mileageBetween(Integer minMileage, Integer maxMileage) {
        return (root, query, cb) -> {
            if (minMileage == null && maxMileage == null) return cb.conjunction();
            if (minMileage == null) return cb.lessThanOrEqualTo(root.get("mileage"), maxMileage);
            if (maxMileage == null) return cb.greaterThanOrEqualTo(root.get("mileage"), minMileage);
            return cb.between(root.get("mileage"), minMileage, maxMileage);
        };
    }
    */
}