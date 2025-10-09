package com.softeng.backend.config;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Reference: I was having trouble getting the firestore emulators to connect through auto-config variables
 * So I asked chatgpt, this code from chatgpt is how we can set up firestore/spring boot to point
 * to the local emulator instance.
 * Source: ChatGPT, "Answer to question about Firestore setup with Spring Boot," OpenAI, ChatGPT model (GPT-5 Mini), Oct. 8, 2025. [Online].
 * Available: https://chat.openai.com/
 */

@Configuration
public class FirestoreConfig {

    @Value("${FIRESTORE_EMULATOR_HOST:}")
    private String emulatorHost;

    @Value("${FIREBASE_PROJECT_ID:qdog-6aca2}")
    private String projectId;

    @Bean
    public Firestore firestore() {
        System.out.println("Emulator host: " + emulatorHost);
        System.out.println("Project ID: " + projectId);
        FirestoreOptions options = FirestoreOptions.getDefaultInstance()
                .toBuilder()
                .setProjectId(projectId)
                .setEmulatorHost(emulatorHost)
                .build();

        return options.getService();
    }
}