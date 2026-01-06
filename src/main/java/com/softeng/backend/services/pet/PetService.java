package com.softeng.backend.services.pet;

import com.softeng.backend.dto.DiaryDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.repository.pet.PetRepository;
import com.softeng.backend.services.user.owner.OwnerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class PetService {

    private final PetRepository petRepository;
    private final OwnerService ownerService;

    @Autowired
    public PetService(PetRepository petRepository, OwnerService ownerService) {
        this.petRepository = petRepository;
        this.ownerService = ownerService;
    }

    // =========================
    // CREATE
    // =========================
    public PetDTO createPet(@Valid Pet pet) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        PetDTO created= petRepository.createPet(pet);
        ownerService.addPet(pet.getOwnerId(), new PetLite(created.getId(), pet.getName(), pet.getBreed()));
        return created;
    }

    // =========================
    // READ
    // =========================
    public PetDTO getPetById(String petId) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        return petRepository.getPetById(petId);
    }

    public List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        return petRepository.getPetsByOwnerId(ownerId);
    }

    // =========================
    // UPDATE
    // =========================
    public PetDTO updatePet(String petId, @Valid Pet pet) throws ExecutionException, InterruptedException, DocumentNotFoundException {

        PetDTO result = petRepository.updatePet(petId, pet);
        Pet updatedPet = result.getPet();
        ownerService.updatePet(pet.getOwnerId(), new PetLite(result.getId(), updatedPet.getName(), updatedPet.getBreed()));

        return result;
    }

    // =========================
    // DELETE
    // =========================
    public PetDTO deletePet(String petId) throws ExecutionException, InterruptedException {
        PetDTO deleted = petRepository.deletePet(petId);
        String ownerId = deleted.getPet().getOwnerId();
        ownerService.removePet(ownerId, petId);
        return deleted;
    }

    // =========================
    // ADD DIARY ENTRY OPERATION
    // =========================
    public DiaryDTO addDiaryEntry(String petId, Diary diary) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        return petRepository.addDiaryEntry(petId, diary);
    }

    // =========================
    // GET DIARY ENTRY IN RANGE OPERATION
    // =========================
    public List<DiaryDTO> getDiaryEntryInRange(String petId,
                                             Date from,
                                             Date to,
                                             int limit) throws ExecutionException, InterruptedException, DocumentNotFoundException {
        return petRepository.getDiaryEntryInRange(petId, from, to, limit);
    }
}
