package com.softeng.backend.controllers.pet;

import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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
    ResponseEntity<Map<String, Object>> addDiaryEntry(@NotNull @NotBlank @PathVariable String petId, @NotNull @Valid @RequestBody Diary diary);

    //GET DIARY ENTRY IN RANGE
    ResponseEntity<ArrayList<Map<String, Object>>> getDiaryEntryInRange(@NotNull @NotBlank @PathVariable String petId,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to,
                                                                        @NotNull int limit);
}
