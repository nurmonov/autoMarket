package org.example.automarket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.example.automarket.entity.enums.Role;
import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email; // Bu bizda username vazifasini bajaradi

    private String password;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN yoki USER

    // Spring Security metodlari
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role asosida huquqlarni qaytaramiz
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email; // Tizimga email orqali kiriladi
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Hisob muddati o'tmagan
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Bloklanmagan
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Parol muddati o'tmagan
    }

    @Override
    public boolean isEnabled() {
        return true; // Foydalanuvchi faol
    }
}