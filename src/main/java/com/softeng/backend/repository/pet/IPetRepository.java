package com.softeng.backend.repository.pet;

import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.models.pet.Pet;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IPetRepository {

    PetDTO createPet(String ownerId, Pet pet) throws ExecutionException, InterruptedException;

    List<PetDTO> getPetById(String ownerId, String petId) throws ExecutionException, InterruptedException;

    List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException;

    PetDTO updatePet(String ownerId, String petId, Pet pet) throws ExecutionException, InterruptedException;

    PetDTO deletePet(String ownerId, String petId) throws ExecutionException, InterruptedException;
}
