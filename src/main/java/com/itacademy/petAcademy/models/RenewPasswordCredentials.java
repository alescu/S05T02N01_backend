package com.itacademy.petAcademy.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(description = "Renew Password Credentials Dto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RenewPasswordCredentials {
    @Schema(name = "username", example = "Pepe", required = true)
    private String username;
    @Schema(name = "password", example = "12345678", required = true)
    private String password;
}
