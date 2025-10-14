package com.softeng.backend.controllers.user;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.services.user.owner.OwnerService;
import com.softeng.backend.services.user.vet.VetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final OwnerService ownerService;
    private final VetService vetService;

    @Autowired
    public AuthController(OwnerService ownerService, VetService vetService) {
        this.ownerService = ownerService;
        this.vetService = vetService;
    }

    @PostMapping("/login")
    ResponseEntity<Map<String, Object>> Login(@RequestBody Map<String, String> loginRequest) {
        ResponseEntity<Map<String, Object>> response;
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        try {
            OwnerDTO ownerDTO;
            ownerDTO = ownerService.getOwnerByEmail(email);
            VetDTO vetDTO;
            vetDTO = vetService.getVetByEmail(email);
            if ((ownerDTO == null || ownerDTO.getId() == null) && (vetDTO == null || vetDTO.getId() == null)) {
                logger.debug("DEBUG LOG: auth/login endpoint not found for email: {}", email);
                response = ResponseEntity.status(400).body(Map.of("error", "Incorrect Credential", "detail", "Email does not exist"));
            } else {
                // TODO: improve security when auth set up
                if (vetDTO != null && vetDTO.getId() != null) {
                    if (!vetDTO.getVet().getPassword().equals(password)) {
                        response = ResponseEntity.status(400).body(Map.of("error", "Incorrect Credential", "detail", "Password is incorrect"));
                    } else {
                        response = ResponseEntity.status(200).body(vetDTO.toMap());
                    }
                } else if (!ownerDTO.getOwner().getPassword().equals(password)) {
                    response = ResponseEntity.status(400).body(Map.of("error", "Incorrect Credential", "detail", "Password is incorrect"));
                } else {
                    response = ResponseEntity.status(200).body(ownerDTO.toMap());
                }
            }
        } catch (Exception e) {
            logger.debug("DEBUG LOG: Auth /login endpoint error for email: {}/n stack trace: {}", email, Arrays.toString(e.getStackTrace()));
            response = ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
        return response;
    }
}
