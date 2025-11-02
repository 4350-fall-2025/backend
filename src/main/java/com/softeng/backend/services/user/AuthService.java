package com.softeng.backend.services.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    /**
     * example code was used from firebase docs for authentication:
     * https://firebase.google.com/docs/auth/admin/create-custom-tokens#create_custom_tokens_using_the_firebase_admin_sdk
     */
    public String createCustomToken(String uid, String role)  {
        try{
            Map<String, Object> additionalClaims = new HashMap<String, Object>();
            additionalClaims.put("role", role);
            return FirebaseAuth.getInstance().createCustomToken(uid, additionalClaims);
        } catch (FirebaseAuthException e) {
            logger.error(e.getMessage());
            return "It failed to create custom token";
        }
    }
}
