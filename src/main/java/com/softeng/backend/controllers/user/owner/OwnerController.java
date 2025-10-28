package com.softeng.backend.controllers.user.owner;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.services.user.owner.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.Arrays;
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

@RestController
@RequestMapping("/api/v1/owners")
public class OwnerController implements IOwnerController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);
    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    /*****************************************************************************
     * SIGNUP/CREATE
     ******************************************************************************/
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> createOwner(@Valid @RequestBody Owner owner) {
        ResponseEntity<Map<String, Object>> response;

        if (owner == null || owner.checkInvalidUser()) {
            Map<String, String> detail = Map.of("firstName", "Name cannot be empty", "lastName", "Name cannot be empty", "email", "Invalid email format", "password", "Must be at least 8 characters");
            logger.debug("DEBUG LOG: Owner /create endpoint detected null request");
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed", "detail", detail));
        }
        try {
            OwnerDTO dto = ownerService.createOwner(owner);
            if (dto == null || dto.isEmpty()) {
                logger.debug("DEBUG LOG: Owner with email: {} already exists", owner.getEmail());
                Map<String, String> detail = Map.of("email", "Email already exists");
                return ResponseEntity.status(409).body(Map.of("error", "Conflict fields", "detail", detail));
            }
            response = ResponseEntity.status(201).location(URI.create("/api/v1/owners/" + dto.getId())).body(dto.toMap());
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /create endpoint not found for owner: {}/n stack trace: {}", owner.getEmail(), Arrays.toString(e.getStackTrace()));
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
            logger.debug("DEBUG LOG: Owner /id endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.isEmpty()) {
            logger.debug("DEBUG LOG: Owner /id endpoint hit with id: {} not found", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(dto.toMap());
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOwner(@PathVariable String id, @Valid @RequestBody Owner owner) {

        OwnerDTO dto;
        if (owner == null || owner.checkEmptyUser()) {
            logger.debug("DEBUG LOG: Owner update /id endpoint detected empty request: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            dto = ownerService.updateOwner(id, owner);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /update endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.isEmpty()) {
            logger.debug("DEBUG LOG: Owner update /id endpoint not found for id: {}", id);
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

        boolean deleted = ownerService.deleteOwner(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            logger.debug("DEBUG LOG: Owner delete /id endpoint not found for id: {}", id);
            return ResponseEntity.internalServerError().build();
        }
    }
}
