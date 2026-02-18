package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    @MapsId("userId")  // Maps the userId part of the composite key to the relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("carAdId")  // Maps the carAdId part of the composite key to the relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_ad_id", nullable = false)
    private CarAd carAd;

    private LocalDateTime addedAt = LocalDateTime.now();
}
