package org.example.automarket.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "Telefon raqami bo'sh bo'lishi mumkin emas")
    @Size(min = 9, max = 13, message = "Telefon raqami +998 formatida bo'lishi kerak")
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Telefon raqami +998 bilan boshlanishi va 9 ta raqamdan iborat bo'lishi kerak")
    private String phone;

    @Email(message = "Email noto'g'ri formatda")
    @Size(max = 100, message = "Email juda uzun")
    private String email;

    @NotBlank(message = "Parol bo'sh bo'lishi mumkin emas")
    @Size(min = 8, max = 100, message = "Parol kamida 8 ta belgidan iborat bo'lishi kerak")
    private String password;

    @NotBlank(message = "Ism familiya bo'sh bo'lishi mumkin emas")
    @Size(min = 2, max = 100, message = "Ism familiya 2-100 ta belgi oralig'ida bo'lishi kerak")
    private String fullName;

    private String region;

    private String city;

}
