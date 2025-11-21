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
@Profile({"firebase", "prod"})
public class FirebaseConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.credentials.location}")
    private Resource firebaseCredentials;

    @Value("${spring.cloud.gcp.database-id}")
    private String databaseId;

    @Bean
    public Firestore firestore() throws FirebaseInitializeException {

        FirebaseOptions options;
        GoogleCredentials googleCredentials;

        if (!firebaseCredentials.exists()) {
            try {
                googleCredentials = GoogleCredentials.getApplicationDefault();
            } catch (IOException e) {
                throw new FirebaseInitializeException(e.getMessage());
            }
        } else {
            try (InputStream serviceAccount = firebaseCredentials.getInputStream()) {
                googleCredentials = GoogleCredentials.fromStream(serviceAccount);
            } catch (IOException e) {
                throw new FirebaseInitializeException();
            }
        }

        options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .setProjectId(projectId)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore(databaseId);
    }
}