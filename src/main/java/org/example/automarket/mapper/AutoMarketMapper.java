package org.example.automarket.mapper;

// AutoMarketMapper.java (MapStruct interface - barcha mappinglar bitta interfaceda, lekin alohida package ga qo'yasiz)
import org.example.automarket.dto.*;
import org.example.automarket.entity.*;
import org.example.automarket.entity.enums.BodyType;
import org.example.automarket.entity.enums.FuelType;
import org.example.automarket.entity.enums.Transmission;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AutoMarketMapper {

    AutoMarketMapper INSTANCE = Mappers.getMapper(AutoMarketMapper.class);

    // User mappings
    UserResponseDto toUserResponseDto(User user);
    List<UserResponseDto> toUserResponseDtoList(List<User> users);
    User toUser(UserRegisterRequest dto);

    // Brand mappings
    BrandResponseDto toBrandResponseDto(Brand brand);
    List<BrandResponseDto> toBrandResponseDtoList(List<Brand> brands);

    @Mapping(target = "name", source = "name")
    Brand toBrand(BrandCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBrandFromRequest(BrandUpdateRequest request, @MappingTarget Brand brand);

    Model toModel(ModelCreateRequest request);

    // Model mappings
    @Mapping(target = "brandName", source = "brand.name")
    ModelResponseDto toModelResponseDto(Model model);
    List<ModelResponseDto> toModelResponseDtoList(List<Model> models);

    // CarAd summary (pagination va listing uchun)
    @Mapping(target = "brandName", source = "model.brand.name")
    @Mapping(target = "modelName", source = "model.name")
    @Mapping(target = "transmission", source = "transmission", qualifiedByName = "enumToString")
    @Mapping(target = "fuelType", source = "fuelType", qualifiedByName = "enumToString")
    @Mapping(target = "bodyType", source = "bodyType", qualifiedByName = "enumToString")
    @Mapping(target = "color", source = "color", qualifiedByName = "enumToString")
    @Mapping(target = "mainImageUrl", expression = "java(getMainImageUrl(carAd))")
//    @Mapping(target = "region", source = "seller.region")
//    @Mapping(target = "currency", constant = "UZS")  // Statik, keyin o'zgartirsa bo'ladi
    CarAdSummaryDto toCarAdSummaryDto(CarAd carAd);

    List<CarAdSummaryDto> toCarAdSummaryDtoList(List<CarAd> carAds);

    // CarAd detail
    @Mapping(target = "brandName", source = "model.brand.name")
    @Mapping(target = "modelName", source = "model.name")
    @Mapping(target = "transmission", source = "transmission", qualifiedByName = "enumToString")
    @Mapping(target = "fuelType", source = "fuelType", qualifiedByName = "enumToString")
    @Mapping(target = "bodyType", source = "bodyType", qualifiedByName = "enumToString")
    @Mapping(target = "color", source = "color", qualifiedByName = "enumToString")
    @Mapping(target = "imageUrls", expression = "java(getImageUrls(carAd))")
//    @Mapping(target = "seller", source = "seller")
//    @Mapping(target = "currency", constant = "UZS")
    CarAdDetailDto toCarAdDetailDto(CarAd carAd);

    // Favorite
    @Mapping(target = "carAdId", source = "carAd.id")
    @Mapping(target = "brandName", source = "carAd.model.brand.name")
    @Mapping(target = "modelName", source = "carAd.model.name")
    @Mapping(target = "mainImageUrl", expression = "java(getMainImageUrl(favorite.getCarAd()))")
//    @Mapping(target = "region", source = "carAd.seller.region")
    FavoriteDto toFavoriteDto(Favorite favorite);

    List<FavoriteDto> toFavoriteDtoList(List<Favorite> favorites);

    // Admin pending
    @Mapping(target = "brandName", source = "model.brand.name")
    @Mapping(target = "modelName", source = "model.name")
    @Mapping(target = "sellerPhone", source = "seller.phone")
    @Mapping(target = "sellerFullName", source = "seller.fullName")
    AdminCarAdPendingDto toAdminCarAdPendingDto(CarAd carAd);

    List<AdminCarAdPendingDto> toAdminCarAdPendingDtoList(List<CarAd> carAds);

    // Create request to entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)  // Service da qo'yiladi
    @Mapping(target = "model", ignore = true)   // Service da topiladi
    @Mapping(target = "transmission", source = "transmission", qualifiedByName = "stringToTransmission")
    @Mapping(target = "fuelType", source = "fuelType", qualifiedByName = "stringToFuelType")
    @Mapping(target = "bodyType", source = "bodyType", qualifiedByName = "stringToBodyType")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    CarAd toCarAd(CarAdCreateRequest request);

    // Update request to entity (partial update)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "transmission", source = "transmission", qualifiedByName = "stringToTransmission")
    @Mapping(target = "fuelType", source = "fuelType", qualifiedByName = "stringToFuelType")
    @Mapping(target = "bodyType", source = "bodyType", qualifiedByName = "stringToBodyType")
    void updateCarAdFromRequest(CarAdUpdateRequest request, @MappingTarget CarAd carAd);

    // Enum converters (DTO larda string sifatida bor, entityda enum)
    @Named("enumToString")
    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }

    @Named("stringToTransmission")
    default Transmission stringToTransmission(String value) {
        return value != null ? Transmission.valueOf(value.toUpperCase()) : null;
    }

    @Named("stringToFuelType")
    default FuelType stringToFuelType(String value) {
        return value != null ? FuelType.valueOf(value.toUpperCase()) : null;
    }

    @Named("stringToBodyType")
    default BodyType stringToBodyType(String value) {
        return value != null ? BodyType.valueOf(value.toUpperCase()) : null;
    }

    // Helper methods for images
    default String getMainImageUrl(CarAd carAd) {
        if (carAd.getImages() == null || carAd.getImages().isEmpty()) {
            return null;
        }
        return carAd.getImages().stream()
                .filter(CarImage::isMain)
                .findFirst()
                .map(CarImage::getImageUrl)
                .orElse(carAd.getImages().get(0).getImageUrl());
    }

    default List<String> getImageUrls(CarAd carAd) {
        if (carAd.getImages() == null) return List.of();
        return carAd.getImages().stream()
                .sorted(Comparator.comparingInt(CarImage::getOrderIndex))
                .map(CarImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
