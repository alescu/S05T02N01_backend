package com.itacademy.petAcademy.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "pets")
public class Pet {
    @Id
    public String id;

    public String petName;

    public String petType;

    public String userName;

    public Integer hunger = 5;

    public Integer happiness = 5;

    public Integer energy = 5;

    public Integer health = 5;

    public List<String> objects = new ArrayList<>();

    public String background;

    public Pet(String petName, String petType, String userName) {
        this.petName = petName;
        this.petType = petType;
        this.userName = userName;
    }
}
