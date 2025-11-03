package com.softeng.backend.repository.pet;

import com.softeng.backend.dto.DiaryDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IPetRepository {

    PetDTO createPet(Pet pet) throws ExecutionException, InterruptedException;

    PetDTO getPetById(String petId) throws ExecutionException, InterruptedException;

    List<PetDTO> getPetsByOwnerId(String ownerId) throws ExecutionException, InterruptedException;

    PetDTO updatePet(String petId, Pet pet) throws ExecutionException, InterruptedException;

    PetDTO deletePet(String petId) throws ExecutionException, InterruptedException;

    DiaryDTO addDiaryEntry(@NotNull @NotBlank String petId, @NotNull Diary diary) throws ExecutionException, InterruptedException;
}
