package com.softeng.backend.services.user.owner;

import com.softeng.backend.exception.user.CreateUserException;
import com.softeng.backend.exception.user.UserNotFoundException;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


// TODO add service/business logic here, remove logic from repo

@Service
public class OwnerService implements IOwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    public String createOwner(Owner owner) throws CreateUserException {
        return ownerRepository.createOwner(owner);
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/
    public Owner getOwnerByEmail(String email) throws UserNotFoundException {
        return ownerRepository.getOwnerByEmail(email);
    }

    public Owner getOwnerById(String id) throws UserNotFoundException {
        return ownerRepository.getOwnerByEmail(id);
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/

    // https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
    public Owner updateOwner(String id, Owner owner) {

        Owner updatedOwner = new Owner();
        if (owner.isEmptyUser() ) {
            return updatedOwner;
        }
        try {
            Map<String, Object> updateFields = new HashMap<>();
            if(owner.getFirstName() != null) {
                updateFields.put("firstName", owner.getFirstName());
            }
            if(owner.getLastName() != null) {
                updateFields.put("lastName", owner.getLastName());
            }
            if(owner.getEmail() != null) {
                updateFields.put("email", owner.getEmail());
            }
            if(owner.getPassword() != null) {
                updateFields.put("password", owner.getPassword());
            }
            updatedOwner =  ownerRepository.updateOwner(id, updateFields);
        } catch (ExecutionException | InterruptedException e ) {

            System.err.println("Error updating owner " + e.getMessage());
        }

        return updatedOwner;
    }

}
