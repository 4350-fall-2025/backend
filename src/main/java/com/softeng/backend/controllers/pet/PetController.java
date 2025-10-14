package com.softeng.backend.controllers.pet;

import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.services.pet.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owners")
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    // CREATE:
    // The following code was copied from OpenAI's ChatGPT (https://chat.openai.com)
    @PostMapping("/{id}/pets")
    public ResponseEntity<PetDTO> createPet(@PathVariable String id, @RequestBody PetDTO petDTO) {
        Pet pet = petDTO.getPet();
        PetDTO result = petService.createPet(id, pet);

        if (result.getId() != null) {
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.notFound().build();
    }

//    // UPDATE:
//
//    // The following code was copied from OpenAI's ChatGPT (https://chat.openai.com)
//    @PutMapping("/{ownerId}/pets")
//    public ResponseEntity<Pet> updatePet(@PathVariable String ownerId, @RequestBody Pet pet) {
//        Owner updated = ownerService.updatePet(ownerId, pet);
//        if (updated != null) return ResponseEntity.ok(updated);
//        return ResponseEntity.notFound().build();
//    }
//
//    // DELETE:
//
//    // The following code was copied from OpenAI's ChatGPT (https://chat.openai.com)
//    @DeleteMapping("/{ownerId}/pets/{petId}")
//    public ResponseEntity<Owner> removePet(@PathVariable String ownerId, @PathVariable String petId) {
//        Owner updated = ownerService.removePet(ownerId, petName);
//        if (updated != null) return ResponseEntity.ok(updated);
//        return ResponseEntity.notFound().build();
//        }
//    }
}
