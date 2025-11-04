package com.softeng.backend.repository.pet;

import com.softeng.backend.dto.DiaryDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IPetRepository {

    PetDTO createPet(Pet pet) throws ExecutionException, InterruptedException, DocumentNotFoundException;

    PetDTO getPetById(String petId) throws ExecutionException, InterruptedException, DocumentNotFoundException;

    List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException, DocumentNotFoundException;

    PetDTO updatePet(String petId, Pet pet) throws ExecutionException, InterruptedException, DocumentNotFoundException;

    PetDTO deletePet(String petId) throws ExecutionException, InterruptedException, DocumentNotFoundException;

    DiaryDTO addDiaryEntry(@NotNull @NotBlank String petId, @NotNull Diary diary) throws ExecutionException, InterruptedException, DocumentNotFoundException;

    List<DiaryDTO> getDiaryEntryInRange(@NotNull @NotBlank String petId,
                                             @NotNull Date from,
                                             @NotNull Date to,
                                             int limit) throws ExecutionException, InterruptedException, DocumentNotFoundException;
}
