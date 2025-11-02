package com.softeng.backend.controllers.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Auth Endpoint
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements IAuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final OwnerService ownerService;
    private final VetService vetService;

    @Autowired
    public AuthController(OwnerService ownerService, VetService vetService) {
        this.ownerService = ownerService;
        this.vetService = vetService;
    }

    /**
     * example code was used from firebase docs for authentication:
     * https://firebase.google.com/docs/auth/admin/create-custom-tokens#create_custom_tokens_using_the_firebase_admin_sdk
     * @param uid
     * @param role
     * @return auth token
     * @throws FirebaseAuthException
     */
    private String createCustomToken(String uid, String role)  {
        try{
            Map<String, Object> additionalClaims = new HashMap<String, Object>();
            additionalClaims.put("role", role);
            return FirebaseAuth.getInstance().createCustomToken(uid, additionalClaims);
        } catch (FirebaseAuthException e) {
            logger.error(e.getMessage());
            return "It failed to create custom token";
        }

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
                    String token = createCustomToken(vetDTO.getId(), role);
                    return ResponseEntity.ok().body(Map.of("user",vetDTO.toMap(), "token", token));
                }
            }
            if (ownerDTO != null && !ownerDTO.isEmpty()) {
                role = "owner";
                if (!ownerDTO.getOwner().getPassword().equals(password)) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Incorrect Credential", "detail", "Password is incorrect"));
                } else {
                    String token = createCustomToken(ownerDTO.getId(), role);
                    return ResponseEntity.ok().body(Map.of("user",ownerDTO.toMap(), "token", token));
                }
            }
            logger.debug("DEBUG LOG: auth/login endpoint not found for email: {}", email);
            return ResponseEntity.badRequest().body(Map.of("error", "Incorrect Credential", "detail", "Email does not exist"));
        } catch (Exception e) {
            logger.debug("DEBUG LOG: Auth /login endpoint error for email: {}/n stack trace: {}", email, Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
}
