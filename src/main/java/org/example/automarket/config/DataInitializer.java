package org.example.automarket.config;


import lombok.RequiredArgsConstructor;
import org.example.automarket.entity.*;
import org.example.automarket.entity.enums.*;
import org.example.automarket.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final CarAdRepository carAdRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {

            if (userRepository.count() > 0) return;

            Random random = new Random();

            // ==============================
            // BRANDS & MODELS
            // ==============================

            List<String> brandNames = List.of(
                    "Toyota", "Chevrolet", "BMW", "Mercedes",
                    "Hyundai", "Kia", "Lexus", "Audi"
            );

            List<Brand> brands = new ArrayList<>();

            for (String name : brandNames) {
                Brand brand = Brand.builder()
                        .name(name)
                        .logoUrl("logo_" + name + ".png")
                        .build();

                brand = brandRepository.save(brand);

                for (int i = 1; i <= 5; i++) {
                    Model model = Model.builder()
                            .name(name + "_Model_" + i)
                            .brand(brand)
                            .build();
                    modelRepository.save(model);
                }

                brands.add(brand);
            }

            List<Model> allModels = modelRepository.findAll();

            // ==============================
            // ADMIN
            // ==============================

            User admin = User.builder()
                    .phone("+998900000000")
                    .password(passwordEncoder.encode("123"))
                    .fullName("Admin User")
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);

            // ==============================
            // USERS (19)
            // ==============================

            List<User> users = new ArrayList<>();

            for (int i = 1; i <= 30; i++) {
                User user = User.builder()
                        .phone("+9989010000" + i)
                        .password(passwordEncoder.encode("123"))
                        .fullName("User " + i)
                        .role(Role.USER)
                        .build();

                users.add(userRepository.save(user));
            }

            users.add(admin);

            // ==============================
            // ENUMS
            // ==============================

            Color[] colors = Color.values();
            FuelType[] fuels = FuelType.values();
            Transmission[] transmissions = Transmission.values();
            BodyType[] bodyTypes = BodyType.values();
            AdStatus[] statuses = AdStatus.values();

            List<CarAd> allAds = new ArrayList<>();

            // ==============================
            // 1) 19 USER × 40 CAR
            // ==============================

            for (User user : users) {

                int count = user.getRole() == Role.ADMIN ? 1000 : 40;

                for (int i = 0; i < count; i++) {

                    CarAd ad = CarAd.builder()
                            .seller(user)
                            .model(allModels.get(random.nextInt(allModels.size())))
                            .year(2010 + random.nextInt(15))
                            .price(BigDecimal.valueOf(5000 + random.nextInt(50000)))
                            .mileage(random.nextInt(300000))
                            .color(colors[random.nextInt(colors.length)])
                            .fuelType(fuels[random.nextInt(fuels.length)])
                            .transmission(transmissions[random.nextInt(transmissions.length)])
                            .bodyType(bodyTypes[random.nextInt(bodyTypes.length)])
                            .description("Test car description " + i)
                            .vin("VIN" + UUID.randomUUID())
                            .stateNumber("01A" + random.nextInt(9999))
                            .status(statuses[random.nextInt(statuses.length)])
                            .isFeatured(random.nextBoolean())
                            .build();

                    ad = carAdRepository.save(ad);

                    // 3 ta rasm
                    for (int j = 0; j < 3; j++) {
                        CarImage image = CarImage.builder()
                                .carAd(ad)
                                .imageUrl("https://picsum.photos/400/300?random=" + random.nextInt(10000))
                                .isMain(j == 0)
                                .orderIndex(j)
                                .build();
                        ad.getImages().add(image);
                    }

                    allAds.add(ad);
                }
            }

            // ==============================
            // FAVORITES
            // ==============================

            for (User user : users) {
                for (int i = 0; i < 10; i++) {

                    CarAd randomAd = allAds.get(random.nextInt(allAds.size()));

                    Favorite favorite = Favorite.builder()
                            .id(new FavoriteId(user.getId(), randomAd.getId()))
                            .user(user)
                            .carAd(randomAd)
                            .build();

                    try {
                        favoriteRepository.save(favorite);
                    } catch (Exception ignored) {}
                }
            }

            System.out.println("🔥 1000+ ta test ma'lumot yaratildi!");
        };
    }
}
