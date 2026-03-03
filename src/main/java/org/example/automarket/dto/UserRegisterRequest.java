package org.example.automarket.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    private String phone;
    private String email;

    private String password;
    private String fullName;

    private String region;

    private String city;

}
