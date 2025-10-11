package com.softeng.backend.models.user.vet;

import com.google.cloud.spring.data.firestore.Document;
import com.softeng.backend.models.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: needs to be implemented
@Data
@NoArgsConstructor
@Document(collectionName = "vets")
public class Vet extends User {
    public Vet(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password);
    }
}