package com.softeng.backend.controllers.user.owner;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.services.pet.PetService;
import com.softeng.backend.services.user.owner.OwnerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Owners Endpoint
 * References:
 * <a href="https://masteringbackend.com/posts/spring-boot">...</a>
 * The following code was developed with guidance from OpenAI's ChatGPT (<a href="https://chat.openai.com">...</a>)
 * - I consulted ChatGPT when I ran into syntax bugs or was unsure how a spring boot item worked.
 * - If I used/copied code from ChatGPT, I added in-line comment to reference it.
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final PetService petService;

    @Autowired
    public OwnerController(OwnerService ownerService,  PetService petService) {
        this.ownerService = ownerService;
        this.petService = petService;
    }

    /*****************************************************************************
     * SIGNUP/CREATE
     ******************************************************************************/
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> createOwner(@Valid @RequestBody Owner owner) {
        ResponseEntity<Map<String, Object>> response;

        if (owner == null || owner.checkInvalidUser()) {
            Map<String, String> detail = Map.of("firstName", "Name cannot be empty", "lastName", "Name cannot be empty", "email", "Invalid email format", "password", "Must be at least 8 characters");
            log.debug("DEBUG LOG: Owner /create endpoint detected null request");
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed", "detail", detail));
        }
        try {
            OwnerDTO dto = ownerService.createOwner(owner);
            if (dto == null || dto.isEmpty()) {
                log.debug("DEBUG LOG: Owner with email: {} already exists", owner.getEmail());
                Map<String, String> detail = Map.of("email", "Email already exists");
                return ResponseEntity.status(409).body(Map.of("error", "Conflict fields", "detail", detail));
            }
            response = ResponseEntity.status(201).location(URI.create("/api/v1/owners/" + dto.getId())).body(dto.toMap());
        } catch (ExecutionException | InterruptedException e) {
            log.debug("DEBUG LOG: Owner /create endpoint not found for owner: {}\n stack trace: {}", owner.getEmail(), Arrays.toString(e.getStackTrace()));
            response = ResponseEntity.internalServerError().build();
        }
        return response;
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/
    //get owner by id
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOwnerById(@PathVariable String id) {
        OwnerDTO dto;
        try {
            dto = ownerService.getOwnerById(id);
        } catch (ExecutionException | InterruptedException e) {
            log.debug("DEBUG LOG: Owner /id endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.isEmpty()) {
            log.debug("DEBUG LOG: Owner /id endpoint hit with id: {} not found", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(dto.toMap());
    }

    @GetMapping("/{ownerId}/pets")
    public ResponseEntity<List<Map<String, Object>>> getOwnersPets(@PathVariable String ownerId) {
        List<Map<String, Object>> result;

        List<PetDTO> pets;
        try {
            pets = petService.getPetsByOwnerId(ownerId);
            // Steam map generated with IntelliJ autocomplete
            result = pets.stream().map(PetDTO::toMap).toList();
            return ResponseEntity.ok().body(result);
        } catch (ExecutionException | InterruptedException e) {
            log.error("ERROR LOG: Endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        } catch (DocumentNotFoundException e) {
            log.info("INFO LOG: Cannot find owner with id: {}", ownerId);
            return ResponseEntity.notFound().build();
        }
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOwner(@PathVariable String id, @Valid @RequestBody Owner owner) {

        OwnerDTO dto;
        if (owner == null || owner.checkEmptyUser()) {
            log.debug("DEBUG LOG: Owner update /id endpoint detected empty request: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            dto = ownerService.updateOwner(id, owner);
        } catch (ExecutionException | InterruptedException e) {
            log.debug("DEBUG LOG: Owner /update endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.isEmpty()) {
            log.debug("DEBUG LOG: Owner update /id endpoint not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(dto.toMap());
    }

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOwner(@PathVariable String id) {
        OwnerDTO dto;
        try {
            dto = ownerService.getOwnerById(id);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            ownerService.deleteOwner(id);
        } catch (ExecutionException | InterruptedException | DocumentNotFoundException e) {
            log.debug("DEBUG LOG: Owner delete /id endpoint not found for id: {}", id);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }
}
