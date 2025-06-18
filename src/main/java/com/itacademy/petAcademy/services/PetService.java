package com.itacademy.petAcademy.services;

import com.itacademy.petAcademy.entities.Pet;
import com.itacademy.petAcademy.repositories.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository){
        this.petRepository = petRepository;
    }

    public Pet getNewPet(String userName, String petName) {
        Pet pet = new Pet(petName, "Cat", userName);
        pet.setEnergy(5);
        pet.setHunger(5);
        pet.setHappiness(5);
        return petRepository.save(pet);
    }

    public Pet getUserPetByPetName(String userName, String petName) {
        return petRepository.findByUserNameAndPetName(userName, petName).orElse(null);
    }

    public Pet getPetByName(String name) {
        return petRepository.findByPetName(name).orElse(null);
    }

    public List<Pet> getPetsByUserName(String userName) {
        return petRepository.findByUserName(userName);
    }

    public List<Pet> getGetAllPets() {
        return petRepository.findAll();
    }

    public Pet savePet(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet createNewPet(String userName, String petName, String type) {
        Pet pet = new Pet(petName, type, userName);
        return petRepository.save(pet);
    }

    public void deletePet(String userName, String petName) {
        Pet pet = getUserPetByPetName(userName, petName);
        petRepository.delete(pet);
    }

}
