package com.softeng.backend.services.pet;

import com.softeng.backend.dto.DiaryDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IPetService {
    PetDTO createPet(Pet pet) throws ExecutionException, InterruptedException;

    PetDTO getPetById(String petId) throws ExecutionException, InterruptedException;

    List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException;

    PetDTO updatePet(String petId, Pet pet) throws ExecutionException, InterruptedException;

    PetDTO deletePet(String petId) throws ExecutionException, InterruptedException;

    DiaryDTO addDiaryEntry(String petId, Diary diary) throws ExecutionException, InterruptedException, DocumentNotFoundException;
}
