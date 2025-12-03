package com.softeng.backend.controllers.user.vet;

import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.services.user.vet.VetService;
import com.softeng.backend.services.user.vet.VetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//  The following code was copied from OpenAI's ChatGPT (https://chat.openai.com)
//  - Asked ChatGPT for basic Controller setup (for testing) when making this
// TODO: needs to be implemented

@RestController
@RequestMapping("/api/v1/vets")
public class VetController implements IVetController {
    private static final Logger logger = LoggerFactory.getLogger(VetController.class);
    private final VetService vetService;

    @Autowired
    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /*****************************************************************************
     * SIGNUP/CREATE
     ******************************************************************************/
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> createVet(@Valid @RequestBody Vet vet) {
        ResponseEntity<Map<String, Object>> response;
        if (vet == null || vet.checkInvalidUser()) {
            Map<String, String> detail = Map.of("firstName", "Name cannot be empty", "lastName", "Name cannot be empty", "email", "Invalid email format", "password", "Must be at least 8 characters", "certification", "Certification cannot be empty");
            logger.debug("DEBUG LOG: Vet /create endpoint detected null request");
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed", "detail", detail));
        }
        try {
            VetDTO dto = vetService.createVet(vet);
            if (dto == null || dto.isEmpty()) {
                logger.debug("DEBUG LOG: Vet with email: {} already exists", vet.getEmail());
                Map<String, String> detail = Map.of("email", "Email already exists");
                return ResponseEntity.status(409).body(Map.of("error", "Conflict fields", "detail", detail));
            }
            response = ResponseEntity.created(URI.create("/api/v1/vets/" + dto.getId())).body(dto.toMap());
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Vet /create endpoint not found for vet: {}/n stack trace: {}", vet.getEmail(), Arrays.toString(e.getStackTrace()));
            response = ResponseEntity.internalServerError().build();
        }
        return response;
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/
    //get vet by id
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVetById(@PathVariable String id) {
        VetDTO dto;
        try {
            dto = vetService.getVetById(id);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Vet /id endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.isEmpty()) {
            logger.debug("DEBUG LOG: Vet /id endpoint hit with id: {} not found", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(dto.toMap());
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateVet(@PathVariable String id, @Valid @RequestBody Vet vet) {

        VetDTO dto;
        if (vet == null || vet.checkEmptyUser()) {
            logger.debug("DEBUG LOG: Vet update /id endpoint detected empty request: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            dto = vetService.updateVet(id, vet);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Vet /update endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.isEmpty()) {
            logger.debug("DEBUG LOG: Vet update /id endpoint not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(dto.toMap());
    }

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVet(@PathVariable String id) {
        VetDTO dto = null;
        try {
            dto = vetService.getVetById(id);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.isEmpty()) {
            logger.debug("DEBUG LOG: Vet delete /id endpoint not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }

        try {
            vetService.deleteVet(id);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: delete /id endpoint failed for id: {}", id);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.noContent().build();
    }
}
