package com.softeng.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.softeng.backend.exception.config.FirebaseInitializeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * References:
 * Used the following video when setting this up:
 * <a href="https://youtu.be/TkoKdO5Knhk?si=vTl2eMINMJVVmM3q">...</a>
 * <a href="https://firebase.google.com/docs/firestore/quickstart#initialize">...</a>
 * The following code was developed with guidance from OpenAI's ChatGPT (<a href="https://chat.openai.com">...</a>)
 * - Asked chatgpt for how to set up using environment variables (appCredentialsPath)
 * - Troubleshooted connection issues with ChatGPT when database connection didn't work
 */

@Configuration
public class FirebaseConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.credentials.location:}")
    private Resource firebaseCredentials;

    @Bean
    public Firestore firestore() throws FirebaseInitializeException {
        try {
            String emulatorHost = System.getenv("FIRESTORE_EMULATOR_HOST");
            String ciSecret = System.getenv("FIREBASE_SERVICE_ACCOUNT"); // JSON from GitHub secret

            FirebaseOptions options;

            if (emulatorHost != null && !emulatorHost.isBlank()) {
                options = FirebaseOptions.builder()
                        .setProjectId(projectId)
                        .setCredentials(GoogleCredentials.create(null))
                        .build();
            } else if (ciSecret != null && !ciSecret.isBlank()) {
                try (InputStream serviceAccount = new ByteArrayInputStream(ciSecret.getBytes(StandardCharsets.UTF_8))) {
                    options = FirebaseOptions.builder()
                            .setProjectId(projectId)
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                }
            } else {
                try (InputStream serviceAccount = firebaseCredentials.getInputStream()) {
                    options = FirebaseOptions.builder()
                            .setProjectId(projectId)
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                }
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            return FirestoreClient.getFirestore();
        } catch (IOException e) {
            throw new FirebaseInitializeException();
        }
    }
}
