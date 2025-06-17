package com.itacademy.petAcademy.repositories;

import com.itacademy.petAcademy.entities.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends MongoRepository<Pet, String> {

    public Optional<Pet> findByPetName(String petName);

    public Optional<Pet> findByUserNameAndPetName(String userName, String petName);

    public List<Pet> findByUserName(String userName);

}
