package com.pcdev.expensemanagerbackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    @Value("classpath:serviceAccountKey.json")
    Resource resourceFile;

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @PostConstruct
    public void initializeFirebaseAuth() throws IOException {

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resourceFile.getInputStream()))
                .setServiceAccountId("firebase-adminsdk-zlfjd@expensemanagerapp-7590d.iam.gserviceaccount.com")
                .build();

        FirebaseApp.initializeApp(options);
        System.out.println("Firebase App Successfully Initialized");
    }

}
