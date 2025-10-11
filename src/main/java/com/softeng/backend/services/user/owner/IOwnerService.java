package com.softeng.backend.services.user.owner;

import com.softeng.backend.exception.user.CreateUserException;
import com.softeng.backend.exception.user.UserNotFoundException;
import com.softeng.backend.models.user.owner.Owner;

/*
* General References:
 * https://masteringbackend.com/posts/spring-boot
* */

public interface IOwnerService {

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    public String createOwner(Owner owner) throws CreateUserException;

    /*****************************************************************************
     * READ
     ******************************************************************************/
    public Owner getOwnerByEmail(String email)  throws UserNotFoundException;
    public Owner getOwnerById(String id) throws UserNotFoundException;

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    public Owner updateOwner(String id, Owner owner);
}
