/**
 * This code was created using ChatGPT, Model GPT-5
 * How to set up admin sdk firebase firestore, storage & auth config for emulators
 */

package com.softeng.backend.config.database;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.StorageClient;
import com.softeng.backend.exception.config.FirebaseInitializeException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("emulator")
public class FirebaseEmulatorConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.firestore.emulator.host}")
    private String firestoreHost;

    @Value("${spring.cloud.gcp.firestore.emulator.port}")
    private String firestorePort;

    // -------------------- Firestore Bean (unchanged) --------------------
    @Bean
    public Firestore firestore() throws FirebaseInitializeException {
        try {
            String emulatorHostPort = firestoreHost + ":" + firestorePort;
            Firestore options;

            if (!emulatorHostPort.isBlank()) {
                options = FirestoreOptions.getDefaultInstance().toBuilder()
                        .setProjectId(projectId)
                        .setHost(emulatorHostPort)
                        .setCredentials(new FirestoreOptions.EmulatorCredentials())
                        .setCredentialsProvider(FixedCredentialsProvider.create(new FirestoreOptions.EmulatorCredentials()))
                        .build()
                        .getService();
            } else {
                throw new FirebaseInitializeException("No emulator host and port provided");
            }

            return options;

        } catch (IOException e) {
            throw new FirebaseInitializeException("Exception occurred when initializing firestore");
        }
    }

    // -------------------- FirebaseApp init (Auth + Storage) --------------------
    @PostConstruct
    public void initFirebaseApp() throws IOException {

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.newBuilder().build())
                    .setStorageBucket(projectId + ".appspot.com")
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    // -------------------- Storage Client Bean --------------------
    @Bean
    public StorageClient storageClient() {
        return StorageClient.getInstance();
    }

    // -------------------- Auth Bean --------------------
    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
