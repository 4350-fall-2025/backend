package com.softeng.backend.services.pet;

import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.dto.PetLiteDTO;
import com.softeng.backend.models.pet.Pet;

import java.util.List;

public interface IPetService {
    PetDTO createPet(String ownerId, Pet pet);

    Pet getPetById(String petId);

    List<PetLiteDTO> getPetsByOwnerId(String ownerId);

    Pet updatePet(String petId, Pet pet);

    Pet deletePet(String petId);
}
