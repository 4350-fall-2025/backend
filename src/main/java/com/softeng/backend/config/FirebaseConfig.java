package com.softeng.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.softeng.backend.exception.config.FirebaseInitializeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
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
    private String firebaseCredentialsPath;

    private final Environment environment;

    public FirebaseConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Primary
    public Firestore firestore() throws IOException {

        String emulatorHost = System.getenv("FIRESTORE_EMULATOR_HOST");
        boolean isEmulator = emulatorHost != null && !emulatorHost.isBlank();
        boolean isCiProfile = environment.acceptsProfiles(Profiles.of("ci"));
        FirebaseOptions options = null;

            if (isEmulator || isCiProfile)
            {
                System.err.println("EMULATORS YESSSS HERE");
                options = FirebaseOptions.builder()
                        .setProjectId(projectId)
                        .setCredentials(GoogleCredentials.create(null))
                        .build();

            } else {
                String creds = System.getenv("GCP_CREDENTIALS_JSON");
                if(creds != null && !creds.isBlank()) {

                    if (creds.contains("firebase-admin.json")) {
                        // it's a file path
                       try {
                            FileInputStream serviceAccount = new FileInputStream(creds);
                            options = FirebaseOptions.builder()
                                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                    .setProjectId(projectId)
                                    .build();
                       } catch(IOException e) {
                            System.err.println("In the first if-block, it failed to open the file.");
                       }
                    } else {
                        // it's the actual creds
                        try{
                            InputStream credsStream = new ByteArrayInputStream(creds.getBytes(StandardCharsets.UTF_8));
                            options = FirebaseOptions.builder()
                                    .setProjectId(projectId)
                                    .setCredentials(GoogleCredentials.fromStream(credsStream))
                                    .build();
                        } catch(IOException e) {
                            System.err.println("In the else-block, it failed to open the file.");
                       }
                    }
                }
            }

        FirebaseApp app = FirebaseApp.getApps().isEmpty()
                ? FirebaseApp.initializeApp(options)
                : FirebaseApp.getInstance();

        return FirestoreClient.getFirestore(app);
    }
}