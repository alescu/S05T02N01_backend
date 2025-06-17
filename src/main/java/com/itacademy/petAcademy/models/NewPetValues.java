package com.itacademy.petAcademy.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Pet Values Dto")
@Data
public class NewPetValues {

    public Integer hunger = 0;

    public Integer happiness = 0;

    public Integer energy = 0;

    public Integer health = 0;

    public Integer hygiene = 0;

    public List<String> objects = new ArrayList<>();

    public String background;

}
