package com.softeng.backend.repository.pet;

import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.pet.PetLite;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IPetRepository {

    Pet createPet(String ownerId, Pet pet) throws ExecutionException, InterruptedException;

    Pet getPetById(String petId) throws ExecutionException, InterruptedException;

    List<PetLite> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException;

    Pet updatePet(String id, Pet pet) throws ExecutionException, InterruptedException;

    Pet deletePet(String id) throws ExecutionException, InterruptedException;
}
