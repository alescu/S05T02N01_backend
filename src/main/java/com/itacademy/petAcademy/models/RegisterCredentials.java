package com.itacademy.petAcademy.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Register Credentials Dto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterCredentials {
    @Schema(name = "username", example = "Pepe", required = true)
    private String username;
    @Schema(name = "password", example = "12345678", required = true)
    private String password;
    @Schema(name = "passwordemail", example = "pepe@xxx.yyy", required = true)
    private String email;
}
