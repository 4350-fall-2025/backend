package com.softeng.backend.exception.user;

import java.io.IOException;

public class CreateUserException extends IOException {
    public CreateUserException(String message) {
        super("Error in account creation: " + message);
    }
}
