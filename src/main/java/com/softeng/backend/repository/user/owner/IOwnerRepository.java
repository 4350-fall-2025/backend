package com.softeng.backend.repository.user.owner;

import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.user.owner.Owner;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IOwnerRepository {

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    public String createOwner(Owner owner);

    /*****************************************************************************
     * READ
     ******************************************************************************/
    public Owner getOwnerByEmail(String email);
    public Owner getOwnerById(String id);

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    public Owner updateOwner(String id, Map<String, Object> updateFields) throws ExecutionException, InterruptedException;
}
