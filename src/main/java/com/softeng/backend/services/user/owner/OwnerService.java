package com.softeng.backend.services.user.owner;

import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class OwnerService implements IOwnerService {

    private final OwnerRepository ownerRepository;
    private static final Logger logger = LoggerFactory.getLogger(OwnerRepository.class);

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }


    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    public String createOwner(Owner owner) throws ExecutionException, InterruptedException {
        String response = "";

            if (owner == null || owner.checkInvalidUser()) {
                logger.debug("DEBUG LOG: Owner service detected invalid user for creation: " +
                        (owner == null ? null : owner.getEmail()));
            } else if (getOwnerByEmail(owner.getEmail()).checkInvalidUser()) {
                logger.debug("DEBUG LOG: Owner service detected user that already exists: " + owner.getEmail());
            }
            response = ownerRepository.createOwner(owner);


        return response;
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/
    public Owner getOwnerByEmail(String email) throws ExecutionException, InterruptedException {

        return ownerRepository.getOwnerByEmail(email);
    }

    public Owner getOwnerById(String id) throws ExecutionException, InterruptedException {

            return ownerRepository.getOwnerByEmail(id);
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/

    // https://firebase.google.com/docs/firestore/manage-data/add-data#set_a_document
    public Owner updateOwner(String id, Owner owner) throws ExecutionException, InterruptedException {

        Owner updatedOwner = new Owner();
        if (owner.checkEmptyUser()) {
            return updatedOwner;
        }

        Map<String, Object> updateFields = new HashMap<>();
        if (owner.getFirstName() != null) {
            updateFields.put("firstName", owner.getFirstName());
        }
        if (owner.getLastName() != null) {
            updateFields.put("lastName", owner.getLastName());
        }
        if (owner.getEmail() != null) {
            updateFields.put("email", owner.getEmail());
        }
        if (owner.getPassword() != null) {
            updateFields.put("password", owner.getPassword());
        }
        updatedOwner = ownerRepository.updateOwner(id, updateFields);
        return updatedOwner;
    }
}
