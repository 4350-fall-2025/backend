package com.softeng.backend.controllers.pet;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.models.pet.Pet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface IPetController {

    // CREATE
    ResponseEntity<Map<String, Object>> createPet(@PathVariable String ownerId, @RequestBody Pet pet);

    // READ
    @GetMapping("/{ownerId}/pets")
    ResponseEntity<List<Map<String, Object>>> getPets(@PathVariable String ownerId, @RequestParam String petId);

    // UPDATE
    ResponseEntity<OwnerDTO> updatePet(@PathVariable String ownerId, @RequestParam String petId, @RequestBody Pet pet);

    // DELETE
    ResponseEntity<OwnerDTO> removePet(@PathVariable String ownerId, @RequestParam String petId);

}
