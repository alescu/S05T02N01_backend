package com.itacademy.petAcademy.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Pet Credentials Dto")
@Data
public class NewPetCredentials {

    private String petName;

    private String petType;

}
