package com.softeng.backend.services.pet;

import com.softeng.backend.dto.DiaryDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.diary.Diary;
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
    public PetDTO createPet(Pet pet) throws ExecutionException, InterruptedException {
        PetDTO result = new PetDTO();
        if (pet == null) {
            log.debug("PetService.createPet: pet reference is null");
        } else {
            result = petRepository.createPet(pet);
            log.info("PetService.createPet: created pet with id {}", result.getId());
        }
        return result;
    }

    // =========================
    // READ
    // =========================
    @Override
    public PetDTO getPetById(String petId) throws ExecutionException, InterruptedException {
        return petRepository.getPetById(petId);
    }

    @Override
    public List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException {
        return petRepository.getPetsByOwnerId(ownerId);
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public PetDTO updatePet(String petId, Pet pet) throws ExecutionException, InterruptedException {
        PetDTO result = new PetDTO();

        if (pet != null && pet.isValid()) {
            result = petRepository.updatePet(petId, pet);
        }

        return result;
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public PetDTO deletePet(String petId) throws ExecutionException, InterruptedException {
        return petRepository.deletePet(petId);
    }

    // =========================
    // ADD DIARY ENTRY OPERATION
    // =========================
    @Override
    public DiaryDTO addDiaryEntry(String petId, Diary diary) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        return petRepository.addDiaryEntry(petId, diary);
    }
}
