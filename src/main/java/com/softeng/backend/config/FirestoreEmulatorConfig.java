package com.softeng.backend.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.softeng.backend.exception.config.FirebaseInitializeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("emulator")
public class FirestoreEmulatorConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.firestore.emulator.host}")
    private String emulatorHost;

    @Value("${spring.cloud.gcp.firestore.emulator.port}")
    private String emulatorPort;

    @Bean
    public Firestore firestore() throws FirebaseInitializeException {
        try {
            String emulatorHostPort = emulatorHost + ":" + emulatorPort;
            Firestore options;

            if (!emulatorHostPort.isBlank()) {
                options = FirestoreOptions.getDefaultInstance().toBuilder()
                        .setProjectId(projectId)
                        .setHost(emulatorHostPort)
                        .setCredentials(new FirestoreOptions.EmulatorCredentials())
                        .setCredentialsProvider(FixedCredentialsProvider.create(new FirestoreOptions.EmulatorCredentials()))
                        .build().getService();
            }
            else {
                throw new FirebaseInitializeException();
            }

            return options;

        } catch (IOException e) {
            throw new FirebaseInitializeException();
        }
    }
}
