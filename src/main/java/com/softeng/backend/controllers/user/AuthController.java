package com.softeng.backend.controllers.user;

import com.softeng.backend.dto.OwnerDTO;
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
    //private final VetService vetService; to add when implement vet login

    @Autowired
    public AuthController(OwnerService ownerService, VetService vetService) {
        this.ownerService = ownerService;
        //this.vetService = vetService;
    }

    @PostMapping("/login")
    ResponseEntity<Map<String, Object>> Login(@RequestBody Map<String, String> loginRequest) {
        ResponseEntity<Map<String, Object>> response;
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        try {
            OwnerDTO dto;
            dto = ownerService.getOwnerByEmail(email);
            if (dto == null || dto.getId() == null) {
                logger.debug("DEBUG LOG: Owner auth/login endpoint not found for email: {}", email);
                response = ResponseEntity.status(400).body(Map.of("error", "Incorrect Credential", "detail", "Email does not exist"));

            } else {
                // TODO: improve security when auth set up
                if (!dto.getOwner().getPassword().equals(password)) {
                    response = ResponseEntity.status(400).body(Map.of("error", "Incorrect Credential", "detail", "Password is incorrect"));
                } else {
                    response = ResponseEntity.status(200).body(Map.of("user", dto, "token", "dummy-jwt-token"));
                }
            }
        } catch (Exception e) {
            logger.debug("DEBUG LOG: Auth /login endpoint error for email: {}/n stack trace: {}", email, Arrays.toString(e.getStackTrace()));
            response = ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
        return response;
    }
}
