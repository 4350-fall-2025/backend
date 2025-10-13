package com.softeng.backend.services.user.owner;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

// Portions of the following CRUD methods were guided by OpenAI's ChatGPT (GPT-5), Oct. 12, 2025.
// The implementation and adaptation were done by the author.

@Service
public class OwnerService implements IOwnerService {

    private final OwnerRepository ownerRepository;
    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }


    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    public OwnerDTO createOwner(Owner owner) throws ExecutionException, InterruptedException {
        OwnerDTO dto;
        if (owner == null || owner.checkInvalidUser()) {
            logger.debug("DEBUG LOG: Owner service detected invalid user for creation: {}", owner == null ? null : owner.getEmail());
            dto = new OwnerDTO();
        } else if (getOwnerByEmail(owner.getEmail()).getId() != null) {
            logger.debug("DEBUG LOG: Owner service detected user that already exists: {}", owner.getEmail());
            dto = new OwnerDTO();
        } else {
            dto = ownerRepository.createOwner(owner);
        }
        return dto;
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/
    public OwnerDTO getOwnerByEmail(String email) throws ExecutionException, InterruptedException {
        return ownerRepository.getOwnerByEmail(email);
    }

    public OwnerDTO getOwnerById(String id) throws ExecutionException, InterruptedException {
        return ownerRepository.getOwnerById(id);
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/

    // https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
    public OwnerDTO updateOwner(String id, Owner owner) throws ExecutionException, InterruptedException {

        if (owner.checkEmptyUser()) {
            return new OwnerDTO();
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
        return ownerRepository.updateOwner(id, updateFields);
    }

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    public boolean deleteOwner(String id) {
        return ownerRepository.deleteOwner(id);
    }
}
