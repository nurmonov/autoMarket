package org.example.automarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "car_ad_id")
    private Long carAdId;

    // equals and hashCode (Lombok @Data handles this, but explicit for clarity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteId that = (FavoriteId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(carAdId, that.carAdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, carAdId);
    }

    @Override
    public String toString() {
        return "FavoriteId{userId=" + userId + ", carAdId=" + carAdId + '}';
    }
}