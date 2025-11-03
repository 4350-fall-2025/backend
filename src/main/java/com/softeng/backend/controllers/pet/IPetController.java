package com.softeng.backend.controllers.pet;

import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public interface IPetController {

    // CREATE
    ResponseEntity<Map<String, Object>> createPet(@NotNull @RequestBody Pet pet);

    // READ
    ResponseEntity<Map<String, Object>> getPet(@NotNull @NotBlank @PathVariable String petId);

    // UPDATE
    ResponseEntity<Map<String, Object>> updatePet(@NotNull @NotBlank @PathVariable String petId, @NotNull @RequestBody Pet pet);

    // DELETE
    ResponseEntity<Map<String, Object>> removePet(@NotNull @NotBlank @PathVariable String petId);

    //CREATE PET DIARY ENTRY
    ResponseEntity<Map<String, Object>> addDiaryEntry(@NotNull @NotBlank @PathVariable String petId, @NotNull @RequestBody Diary diary);
}
