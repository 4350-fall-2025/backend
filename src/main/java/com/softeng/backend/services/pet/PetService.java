package com.softeng.backend.services.pet;

import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.dto.PetLiteDTO;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.repository.pet.IPetRepository;
import com.softeng.backend.repository.pet.PetRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PetService implements IPetService {

    private final PetRepository petRepository;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PetService.class);

    @Autowired
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    // =========================
    // CREATE
    // =========================
    @Override
    public PetDTO createPet(String ownerId, Pet pet) {
        Pet result = new Pet();

        try {
            // TODO validate pet and ownerId
            if (pet == null) {
                logger.debug("PetService.createPet: pet reference is null");
            } else {
                result = petRepository.createPet(ownerId, pet);
                logger.info("PetService.createPet: created pet with id {}", result.getId());
            }

            return new PetDTO(result.getId(), result);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // READ
    // =========================
    @Override
    public Pet getPetById(String petId) {
//        try {
//            return repository.getPetById(petId);
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    @Override
    public List<PetLiteDTO> getPetsByOwnerId(String ownerId) {
//        try {
//            return repository.getPetsByOwnerId(ownerId);
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        return null;
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public Pet updatePet(String petId, Pet pet) {
//        try {
//            return repository.updatePet(petId, pet);
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public Pet deletePet(String petId) {
//        try {
//            return repository.deletePet(petId);
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        return null;
    }
}
