package com.softeng.backend.exception.config;

import java.io.IOException;

public class FirebaseInitializeException extends IOException {

    public FirebaseInitializeException() {
        super("Error initializing Firebase Services.");
    }
}
