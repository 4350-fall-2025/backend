package com.softeng.backend.services.pet;

import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.dto.PetLiteDTO;
import com.softeng.backend.models.pet.Pet;

import java.util.List;

public interface IPetService {
    PetDTO createPet(String ownerId, Pet pet);

    PetDTO getPetById(String petId);

    List<PetLiteDTO> getPetsByOwnerId(String ownerId);

    PetDTO updatePet(String petId, Pet pet);

    PetDTO deletePet(String petId);
}
