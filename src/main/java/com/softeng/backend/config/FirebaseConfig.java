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
 * https://youtu.be/TkoKdO5Knhk?si=vTl2eMINMJVVmM3q
 * https://firebase.google.com/docs/firestore/quickstart#initialize
 * The following code was developed with guidance from OpenAI's ChatGPT (https://chat.openai.com)
 * - Asked chatgpt for how to set up using environment variables (appCredentialsPath)
 * - Troubleshooted connection issues with ChatGPT when database connection didn't work
 */


@Configuration
public class FirebaseConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.credentials.location:}")
    private Resource firebaseCredentials;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Bean
    public Firestore firestore() throws IOException {
        String emulatorHost = System.getenv("FIRESTORE_EMULATOR_HOST");
        boolean isEmulator = emulatorHost != null && !emulatorHost.isBlank();
        boolean isCiProfile = "ci".equalsIgnoreCase(activeProfile);

        FirebaseOptions options;

        if (isEmulator || isCiProfile) {
            String credsJson = System.getenv("GCP_CREDENTIALS_JSON");

            if (credsJson != null && !credsJson.isBlank()) {
                try (InputStream credsStream = new ByteArrayInputStream(credsJson.getBytes(StandardCharsets.UTF_8))) {
                    options = FirebaseOptions.builder()
                            .setProjectId(projectId)
                            .setCredentials(GoogleCredentials.fromStream(credsStream))
                            .build();
                }
            } else {
                options = FirebaseOptions.builder()
                        .setProjectId(projectId)
                        .setCredentials(GoogleCredentials.create(null))
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

        FirebaseApp app;
        if (FirebaseApp.getApps().isEmpty()) {
            app = FirebaseApp.initializeApp(options);
        } else {
            app = FirebaseApp.getInstance();
        }

        return FirestoreClient.getFirestore(app);
    }
}
