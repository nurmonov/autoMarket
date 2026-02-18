package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.automarket.entity.enums.Role;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {  // Spring Security uchun UserDetails implement qilindi
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phone;  // +998... asosiy identifier

    @Column(unique = true)
    private String email;

    private String password;  // BCrypt encoded (Security da ishlatiladi)

    private String fullName;

    private String region;  // Toshkent, Samarqand...

    private String city;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;  // Default USER

    private boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // UserDetails methods (Spring Security uchun)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));  // ROLE_USER, ROLE_ADMIN...
    }

    @Override
    public String getUsername() {
        return phone;  // Phone ni username sifatida ishlatamiz
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}