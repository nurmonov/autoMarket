package org.example.automarket.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private String phone;
    private String fullName;
    private String role;
}
