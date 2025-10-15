package com.softeng.backend.services.pet;

import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.repository.pet.IPetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class PetService implements IPetService {

    private final IPetRepository petRepository;

    @Autowired
    public PetService(IPetRepository petRepository) {
        this.petRepository = petRepository;
    }

    // =========================
    // CREATE
    // =========================
    @Override
    public PetDTO createPet(String ownerId, Pet pet) throws ExecutionException, InterruptedException {
        PetDTO result = new PetDTO();
        if (pet == null) {
            log.debug("PetService.createPet: pet reference is null");
        } else {
            result = petRepository.createPet(ownerId, pet);
            log.info("PetService.createPet: created pet with id {}", result.getId());
        }
        return result;
    }

    // =========================
    // READ
    // =========================
    @Override
    public List<PetDTO> getPetById(String ownerId, String petId) throws ExecutionException, InterruptedException {
        return petRepository.getPetById(ownerId, petId);
    }

    @Override
    public List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException {
        return petRepository.getPetsByOwnerId(ownerId);
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public PetDTO updatePet(String ownerId, String petId, Pet pet) throws ExecutionException, InterruptedException {
        PetDTO result = new PetDTO();

        if (pet != null && pet.isValid()) {
            result = petRepository.updatePet(ownerId, petId, pet);
        }

        return result;
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public PetDTO deletePet(String ownerId, String petId) throws ExecutionException, InterruptedException {
        return petRepository.deletePet(ownerId, petId);
    }
}
