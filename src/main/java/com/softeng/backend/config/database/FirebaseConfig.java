package com.softeng.backend.config.database;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.softeng.backend.exception.config.FirebaseInitializeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * References:
 * Used the following video when setting this up:
 * https://youtu.be/TkoKdO5Knhk?si=vTl2eMINMJVVmM3q
 * https://firebase.google.com/docs/firestore/quickstart#initialize
 * The following code was developed with guidance from OpenAI's ChatGPT (https://chat.openai.com)
 * - Asked chatgpt for how to set up using environment variables (appCredentialsPath)
 * - Troubleshooted connection issues with ChatGPT when database connection didn't work
 */


@Configuration
@Profile("firebase")
public class FirebaseConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.credentials.location:}")
    private Resource firebaseCredentials;

    @Bean
    public Firestore firestore() throws FirebaseInitializeException {

        FirebaseOptions options;

        try (InputStream serviceAccount = firebaseCredentials.getInputStream()) {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();
        } catch (IOException e) {
            throw new FirebaseInitializeException();
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
}