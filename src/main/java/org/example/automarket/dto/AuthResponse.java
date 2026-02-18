package org.example.automarket.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String phone;
    private String fullName;
    private String role;
}
