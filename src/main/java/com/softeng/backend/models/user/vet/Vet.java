package com.softeng.backend.models.user.vet;

import com.google.cloud.spring.data.firestore.Document;
import com.softeng.backend.models.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Document(collectionName = "vets")
public class Vet extends User {
    private String certification;

    public Vet(String firstName, String lastName, String email, String password, String certification) {
        super(firstName, lastName, email, password);
        this.certification = certification;
    }
}