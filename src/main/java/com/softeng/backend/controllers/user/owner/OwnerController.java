package com.softeng.backend.controllers.user.owner;

import com.softeng.backend.exception.user.CreateUserException;
import com.softeng.backend.exception.user.UserNotFoundException;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.services.user.owner.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
        } catch (CreateUserException e) {
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
            if (owner.isNullUser()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(owner);
        } catch (UserNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping
    public ResponseEntity<Owner> getOwnerById(@Valid @RequestParam String id) {
        Owner owner = null;
        try {
            owner = ownerService.getOwnerById(id);
            if (owner.isNullUser()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(owner);
        } catch (UserNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
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
        } catch (UserNotFoundException e) {
            response = ResponseEntity.internalServerError().body(e.getMessage());
        }

        if (owner == null) {
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

        Owner updatedOwner = ownerService.updateOwner(id, owner);
        if (updatedOwner == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updatedOwner);
        }
    }

}
