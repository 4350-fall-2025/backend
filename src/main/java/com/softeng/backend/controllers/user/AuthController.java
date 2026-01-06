package com.softeng.backend.controllers.user;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.services.user.AuthService;
import com.softeng.backend.services.user.owner.OwnerService;
import com.softeng.backend.services.user.vet.VetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Arrays;
import java.util.Map;

/**
 * Auth Endpoint
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final OwnerService ownerService;
    private final VetService vetService;
    private final AuthService authService;

    @Autowired
    public AuthController(OwnerService ownerService, VetService vetService, AuthService authService) {
        this.ownerService = ownerService;
        this.vetService = vetService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> Login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        String role;
        try {
            OwnerDTO ownerDTO;
            ownerDTO = ownerService.getOwnerByEmail(email);
            VetDTO vetDTO;
            vetDTO = vetService.getVetByEmail(email);

            // TODO: improve security when auth set up
            if (vetDTO != null && !vetDTO.isEmpty()) {
                role = "vet";
                if (!vetDTO.getVet().getPassword().equals(password)) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Incorrect Credential", "detail", "Password is incorrect"));
                } else {
                    String token = authService.createCustomToken(vetDTO.getId(), role);
                    vetDTO = new VetDTO(vetDTO, token);
                    return ResponseEntity.ok().body(vetDTO.toMap());
                }
            }
            if (ownerDTO != null && !ownerDTO.isEmpty()) {
                role = "owner";
                if (!ownerDTO.getOwner().getPassword().equals(password)) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Incorrect Credential", "detail", "Password is incorrect"));
                } else {
                    String token = authService.createCustomToken(ownerDTO.getId(), role);
                    ownerDTO = new OwnerDTO(ownerDTO, token);
                    return ResponseEntity.ok().body(ownerDTO.toMap());
                }
            }
            log.debug("DEBUG LOG: auth/login endpoint not found for email: {}", email);
            return ResponseEntity.badRequest().body(Map.of("error", "Incorrect Credential", "detail", "Email does not exist"));
        } catch (Exception e) {
            log.debug("DEBUG LOG: Auth /login endpoint error for email: {}\n stack trace: {}", email, Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
}
