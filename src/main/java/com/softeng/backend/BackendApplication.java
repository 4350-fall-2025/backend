package com.softeng.backend;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        String activeProfile = System.getProperty("spring.profiles.active", "");

        SpringApplicationBuilder builder = new SpringApplicationBuilder(BackendApplication.class);

        if ("ci".equals(activeProfile)) {
            builder.profiles("ci")
                   .properties("spring.autoconfigure.exclude=com.google.cloud.spring.autoconfigure.firestore.GcpFirestoreAutoConfiguration");
        }

        builder.run(args);
    }
}
