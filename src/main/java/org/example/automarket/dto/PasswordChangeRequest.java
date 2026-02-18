package org.example.automarket.dto;

import lombok.Data;

@Data
public class PasswordChangeRequest {

    private String oldPassword;


    private String newPassword;
}
