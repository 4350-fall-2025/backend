package com.softeng.backend.controllers.pet;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.models.pet.Pet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface IPetController {

    // CREATE
    ResponseEntity<PetDTO> createPet(@RequestBody Pet pet);

    // READ
    ResponseEntity<PetDTO> getPet(@PathVariable String petId);

    // UPDATE
    ResponseEntity<PetDTO> updatePet(@PathVariable String petId, @RequestBody Pet pet);

    // DELETE
    ResponseEntity<OwnerDTO> removePet(@PathVariable String petId);

}
