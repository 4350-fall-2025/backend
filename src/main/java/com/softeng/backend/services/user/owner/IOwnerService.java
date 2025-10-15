package com.softeng.backend.services.user.owner;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.user.owner.Owner;

import java.util.concurrent.ExecutionException;

/*
* General References:
 * https://masteringbackend.com/posts/spring-boot
* */

public interface IOwnerService {

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    OwnerDTO createOwner(Owner owner) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * READ
     ******************************************************************************/
    OwnerDTO getOwnerByEmail(String email)  throws ExecutionException, InterruptedException;
    OwnerDTO getOwnerById(String id) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    OwnerDTO updateOwner(String id, Owner owner) throws ExecutionException, InterruptedException;

    OwnerDTO updatePet(String ownerId, Pet pet) throws ExecutionException, InterruptedException;

    OwnerDTO removePet(String ownerId, String petId) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    boolean deleteOwner(String id);

}
