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
     * CREATE
     ******************************************************************************/

    //implement but not used, use signup endpoint instead
    @PostMapping("/create")
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody Owner owner) {
        ResponseEntity<OwnerDTO> response;
        try {
            OwnerDTO dto = ownerService.createOwner(owner);
            response = ResponseEntity.created(URI.create("/" + dto.getId())).body(dto);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /create endpoint not found for owner: {}/n stack trace: {}", owner.getEmail(), Arrays.toString(e.getStackTrace()));
            response = ResponseEntity.badRequest().build();
        }
        return response;
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/

    //get owner by id
    @GetMapping("/{id}")
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable String id) {
        OwnerDTO dto;
        try {
            dto = ownerService.getOwnerById(id);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /id endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto.getId() == null) {
            logger.debug("DEBUG LOG: Owner /id endpoint hit with id: {} not found", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dto);
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/

    // Login
    @PostMapping("/auth/login")
    public ResponseEntity<OwnerDTO> ownerLogin(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        ResponseEntity<OwnerDTO> response;
        OwnerDTO dto;

        try {
            dto = ownerService.getOwnerByEmail(email);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /aut/login failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.getId() == null) {

            logger.debug("DEBUG LOG: Owner auth/login endpoint not found for email: {}", email);
            response = ResponseEntity.notFound().build();

        } else {
            // TODO: improve security when auth set up
            if (!dto.getOwner().getPassword().equals(password)) {
                response = ResponseEntity.badRequest().build();
            } else {
                response = ResponseEntity.ok(dto);
            }
        }
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable String id, @Valid @RequestBody Owner owner) {

        OwnerDTO dto;
        if (owner.checkEmptyUser()) {
            logger.debug("DEBUG LOG: Owner update /id endpoint detected empty request: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            dto = ownerService.updateOwner(id, owner);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /update endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.getOwner() == null) {
            logger.debug("DEBUG LOG: Owner update /id endpoint not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOwner(@PathVariable String id) {
        OwnerDTO dto;
        try {
            dto = ownerService.getOwnerById(id);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
        if (dto.getId() == null) {
            return ResponseEntity.status(404).build();
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
