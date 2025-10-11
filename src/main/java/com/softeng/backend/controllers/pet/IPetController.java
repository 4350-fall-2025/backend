package com.softeng.backend.controllers.pet;

import com.softeng.backend.models.pet.Pet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface IPetController {

    // CREATE
    public ResponseEntity<Pet> createPet(@PathVariable String id, @RequestBody Pet pet);

    // READ


    // UPDATE
    public ResponseEntity<Pet> updatePet(@PathVariable String ownerId, @RequestBody Pet pet);

    // DELETE
}
