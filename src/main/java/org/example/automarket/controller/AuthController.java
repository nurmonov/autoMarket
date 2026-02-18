package org.example.automarket.controller;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.automarket.dto.LoginRequest;
import org.example.automarket.dto.UserRegisterRequest;
import org.example.automarket.dto.AuthResponse;
import org.example.automarket.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Ro'yxatdan o'tish
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Kirish (login)
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
