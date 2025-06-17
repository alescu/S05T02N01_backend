package com.itacademy.petAcademy.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Login Credentials Dto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginCredentials {
    private String username;
    private String password;
}
