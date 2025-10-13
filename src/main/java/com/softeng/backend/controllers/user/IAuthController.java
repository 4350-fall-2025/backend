package com.softeng.backend.controllers.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface IAuthController {
    /*****************************************************************************
     * LOGIN
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> Login(@RequestBody Map<String, String> loginRequest);
}
