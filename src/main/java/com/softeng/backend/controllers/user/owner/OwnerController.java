package com.softeng.backend.controllers.user.owner;

import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.services.user.owner.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.concurrent.ExecutionException;

/**
 * Owners Endpoint
 * References:
 * https://masteringbackend.com/posts/spring-boot
 *
 * I consulted ChatGPT when I ran into syntax bugs or was unsure how a spring boot item worked.
 * When I used/copied code from ChatGPT, I added in-line comment to reference it.
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
     * CREATE
     ******************************************************************************/

    @PostMapping("/create")
    public ResponseEntity<String> createOwner(@Valid @RequestBody Owner owner) {
        ResponseEntity<String> response;
        try {
            String ownerId = ownerService.createOwner(owner);
            response = ResponseEntity.created(URI.create("/" + ownerId)).body(ownerId);
        } catch (ExecutionException | InterruptedException e) {

            logger.debug("DEBUG LOG: Owner /create endpoint not found for owner: " + owner.getEmail()
                    + "/n stack trace: " + e.getStackTrace());
            response = ResponseEntity.badRequest().body(e.getMessage());
        }
        return response;
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/

    // Get owner by email
    @GetMapping("/email")
    public ResponseEntity<Owner> getOwnerByEmail(@Valid @RequestParam String email) {
        Owner owner = null;
        try {
            owner = ownerService.getOwnerByEmail(email);
            if (owner.checkInvalidUser()) {
                logger.debug("DEBUG LOG: Owner /email endpoint hit with email: " + owner.getEmail() + " not found");
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(owner);

        } catch (ExecutionException | InterruptedException e) {

            logger.debug("DEBUG LOG: Owner /email endpoint failing: " + e.getStackTrace());
            return ResponseEntity.internalServerError().build();

        }
    }


    @GetMapping
    public ResponseEntity<Owner> getOwnerById(@Valid @RequestParam String id) {
        Owner owner = null;
        try {
            owner = ownerService.getOwnerById(id);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /id endpoint failed: " + e.getStackTrace());
            return ResponseEntity.internalServerError().build();
        }

        if (owner.checkInvalidUser()) {
            logger.debug("DEBUG LOG: Owner /id endpoint hit with id: " + id + " not found");
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(owner);
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/

    // Login
    @PostMapping("/auth/login")
    public ResponseEntity<String> ownerLogin(@Valid @RequestParam String email, @Valid @RequestParam String password) {
        ResponseEntity<String> response = null;
        Owner owner = null;

        try {
            owner = ownerService.getOwnerByEmail(email);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /aut/login failed: " + e.getStackTrace());
            response = ResponseEntity.internalServerError().body(e.getMessage());
        }

        if (owner == null) {
            
            logger.debug("DEBUG LOG: Owner auth/login endpoint not found for email: " + email);
            response = ResponseEntity.notFound().build();

        } else {
            if (!owner.getPassword().equals(password)) {
                response = ResponseEntity.badRequest().build();
            } else {
                response = ResponseEntity.ok(owner.getId());
            }
        }
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Owner> updateOwner(@PathVariable String id, @Valid @RequestBody Owner owner) {

        Owner updatedOwner = null;
        try {
            updatedOwner = ownerService.updateOwner(id, owner);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /update endpoint failed: " + e.getStackTrace());
            return ResponseEntity.internalServerError().build();
        }

        if (updatedOwner == null) {
            logger.debug("DEBUG LOG: Owner update /id endpoint not found for id: " + id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedOwner);
    }

}
