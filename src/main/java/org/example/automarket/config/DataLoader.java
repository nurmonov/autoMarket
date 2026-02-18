//package org.example.automarket.config;
//
//
//import lombok.RequiredArgsConstructor;
//import org.example.automarket.entity.*;
//import org.example.automarket.entity.enums.*;
//import org.example.automarket.repo.*;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Component
//@RequiredArgsConstructor
//public class DataLoader implements ApplicationRunner {
//
//    private final UserRepository userRepository;
//    private final BrandRepository brandRepository;
//    private final ModelRepository modelRepository;
//    private final CarAdRepository carAdRepository;
//    private final FavoriteRepository favoriteRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(ApplicationArguments args) {
//
//        if (userRepository.count() > 0) return;
//
//        // =======================
//        // 1️⃣ ADMIN
//        // =======================
//        User admin = User.builder()
//                .phone("+998900000000")
//                .email("admin@gmail.com")
//                .password(passwordEncoder.encode("1234"))
//                .fullName("System Admin")
//                .role(Role.ADMIN)
//                .build();
//
//        userRepository.save(admin);
//
//        // =======================
//        // 2️⃣ BRANDS + MODELS
//        // =======================
//        Brand chevrolet = brandRepository.save(
//                Brand.builder().name("Chevrolet").build());
//
//        Brand toyota = brandRepository.save(
//                Brand.builder().name("Toyota").build());
//
//        Model malibu = modelRepository.save(
//                Model.builder().name("Malibu").brand(chevrolet).build());
//
//        Model cobalt = modelRepository.save(
//                Model.builder().name("Cobalt").brand(chevrolet).build());
//
//        Model camry = modelRepository.save(
//                Model.builder().name("Camry").brand(toyota).build());
//
//        List<Model> models = List.of(malibu, cobalt, camry);
//
//        // =======================
//        // 3️⃣ SELLER USERS (5 ta)
//        // =======================
//        List<User> sellers = new ArrayList<>();
//
//        for (int i = 1; i <= 5; i++) {
//            User user = User.builder()
//                    .phone("+99890111111" + i)
//                    .email("seller" + i + "@gmail.com")
//                    .password(passwordEncoder.encode("1234"))
//                    .fullName("Seller " + i)
//                    .role(Role.USER)
//                    .build();
//
//            sellers.add(userRepository.save(user));
//        }
//
//        // =======================
//        // 4️⃣ HAR BIR SELLER → 5 tadan CAR
//        // =======================
//        List<CarAd> allAds = new ArrayList<>();
//
//        for (User seller : sellers) {
//
//            for (int i = 1; i <= 5; i++) {
//
//                CarAd ad = CarAd.builder()
//                        .seller(seller)
//                        .model(models.get(new Random().nextInt(models.size())))
//                        .year(2018 + i)
//                        .price(BigDecimal.valueOf(10000 + i * 2000))
//                        .mileage(50000 + i * 10000)
//                        .color(Color.QORA)
//                        .fuelType(FuelType.PETROL)
//                        .transmission(Transmission.AVTOMAT)
//                        .bodyType(BodyType.SEDAN)
//                        .description("Clean car, no accident")
//                        .status(AdStatus.APPROVED)
//                        .approvedAt(LocalDateTime.now())
//                        .build();
//
//                allAds.add(carAdRepository.save(ad));
//            }
//        }
//
//        // =======================
//        // 5️⃣ BUYER USERS (5 ta)
//        // =======================
//        List<User> buyers = new ArrayList<>();
//
//        for (int i = 1; i <= 5; i++) {
//            User user = User.builder()
//                    .phone("+99890999999" + i)
//                    .email("buyer" + i + "@gmail.com")
//                    .password(passwordEncoder.encode("1234"))
//                    .fullName("Buyer " + i)
//                    .role(Role.USER)
//                    .build();
//
//            buyers.add(userRepository.save(user));
//        }
//
//        // =======================
//        // 6️⃣ FAVORITES
//        // =======================
//        Random random = new Random();
//
//        for (User buyer : buyers) {
//
//            for (int i = 0; i < 3; i++) { // har buyer 3 ta favorite
//
//                CarAd randomAd = allAds.get(random.nextInt(allAds.size()));
//
//                Favorite favorite = new Favorite(
//                        new FavoriteId(buyer.getId(), randomAd.getId()),
//                        buyer,
//                        randomAd,
//                        LocalDateTime.now()
//                );
//
//                favoriteRepository.save(favorite);
//            }
//        }
//
//        System.out.println("🔥 TEST DATA LOADED SUCCESSFULLY 🔥");
//    }
//}
//
