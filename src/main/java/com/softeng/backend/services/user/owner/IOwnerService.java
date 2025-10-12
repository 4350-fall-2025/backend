package com.softeng.backend.services.user.owner;

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
    public String createOwner(Owner owner) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * READ
     ******************************************************************************/
    public Owner getOwnerByEmail(String email)  throws ExecutionException, InterruptedException;
    public Owner getOwnerById(String id) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    public Owner updateOwner(String id, Owner owner) throws ExecutionException, InterruptedException;
}
