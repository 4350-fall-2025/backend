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
import java.util.List;
import java.util.Map;

public interface IPetController {

    // CREATE
    ResponseEntity<Map<String, Object>> createPet(@NotNull @Valid @RequestBody Pet pet);

    // READ
    ResponseEntity<Map<String, Object>> getPet(@NotNull @NotBlank @PathVariable String petId);

    // UPDATE
    ResponseEntity<Map<String, Object>> updatePet(@NotNull @NotBlank @PathVariable String petId, @NotNull @Valid @RequestBody Pet pet);

    // DELETE
    ResponseEntity<Map<String, Object>> removePet(@NotNull @NotBlank @PathVariable String petId);

    //CREATE PET DIARY ENTRY
    ResponseEntity<Map<String, Object>> addDiaryEntry(@NotNull @NotBlank @PathVariable String petId, @NotNull @Valid @RequestBody Diary diary);

    //GET DIARY ENTRY IN RANGE
    ResponseEntity<List<Map<String, Object>>> getDiaryEntryInRange(@NotNull @NotBlank @PathVariable String petId,
                                                                   @RequestParam(defaultValue = "1970-01-01T00:00:00Z") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
                                                                   @RequestParam(defaultValue = "9999-12-31T00:00:00Z") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to,
                                                                   @RequestParam(defaultValue = "1000") @NotNull int limit);
}
