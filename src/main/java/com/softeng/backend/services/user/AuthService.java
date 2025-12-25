package com.softeng.backend.services.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {

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
            log.error(e.getMessage());
            return null;
        }
    }
}
