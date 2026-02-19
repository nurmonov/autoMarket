package org.example.automarket.service;


import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.AuthResponse;
import org.example.automarket.dto.LoginRequest;
import org.example.automarket.dto.UserRegisterRequest;
import org.example.automarket.entity.User;
import org.example.automarket.repo.UserRepository;
import org.example.automarket.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtService;
    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * Yangi foydalanuvchi ro'yxatdan o'tkazadi va JWT token qaytaradi
     */
    public AuthResponse register(UserRegisterRequest request) {
        User user = userService.register(request);
        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer ",
                user.getPhone(),
                user.getFullName(),
                user.getRole().name()
        );
    }

    /**
     * Login qiladi va muvaffaqiyatli bo'lsa JWT token qaytaradi
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BadCredentialsException("Telefon raqami yoki parol noto'g'ri"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Telefon raqami yoki parol noto'g'ri");
        }

        if (!user.isActive()) {
            throw new DisabledException("Hisobingiz faol emas");
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer ")
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

}
