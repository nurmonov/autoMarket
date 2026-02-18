package org.example.automarket.repo;

// UserRepository.java
import org.example.automarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByPhone(String phone);  // Login va unique check uchun

    boolean existsByPhone(String phone);       // Ro'yxatdan o'tishda check

    Optional<User> findByEmail(String email);  // Agar email ishlatilsa
}


