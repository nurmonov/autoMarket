package org.example.automarket.service;


import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.AuthResponse;
import org.example.automarket.dto.LoginRequest;
import org.example.automarket.dto.UserRegisterRequest;
import org.example.automarket.entity.User;
import org.example.automarket.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Yangi foydalanuvchi ro'yxatdan o'tkazadi va JWT token qaytaradi
     */
    public AuthResponse register(UserRegisterRequest request) {
        User user = userService.register(request);
        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getPhone(),
                user.getFullName(),
                user.getRole().name()
        );
    }

    /**
     * Login qiladi va muvaffaqiyatli bo'lsa JWT token qaytaradi
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Agar User entity UserDetails ni implement qilgan bo'lsa, to'g'ridan-to'g'ri ishlatamiz
        User user = userService.findByPhone(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                user.getPhone(),
                user.getFullName(),
                user.getRole().name()
        );
    }
}
