package com.softeng.backend.repository.user.owner;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.models.user.owner.Owner;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IOwnerRepository {

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    OwnerDTO createOwner(Owner owner) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * READ
     ******************************************************************************/
    OwnerDTO getOwnerByEmail(String email) throws ExecutionException, InterruptedException;
    OwnerDTO getOwnerById(String id) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    OwnerDTO updateOwner(String id, Map<String, Object> updateFields) throws ExecutionException, InterruptedException;

    void addPet(String ownerId, PetLite pet) throws ExecutionException, InterruptedException, DocumentNotFoundException;
    void updatePet(String ownerId, PetLite subdocument) throws ExecutionException, InterruptedException, DocumentNotFoundException;
    void removePet(String ownerId, String petId) throws ExecutionException, InterruptedException, DocumentNotFoundException;

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
     boolean deleteOwner(String id);
}
