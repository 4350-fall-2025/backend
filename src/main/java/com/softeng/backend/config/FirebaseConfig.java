package com.softeng.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.softeng.backend.exception.config.FirebaseInitializeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

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

    @Value("${GOOGLE_APPLICATION_CREDENTIALS:}")
    private String appCredentialsPath;

    @Bean
    public Firestore initializeApp() throws FirebaseInitializeException {

        Firestore app;
        try {

            FileInputStream serviceAccount = new FileInputStream(appCredentialsPath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            return FirestoreClient.getFirestore();

        } catch(IOException e) {
            throw new FirebaseInitializeException();
        }
    }
}
