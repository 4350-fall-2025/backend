package com.softeng.backend.models.user.owner;

import com.google.cloud.spring.data.firestore.Document;
import com.softeng.backend.models.user.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collectionName = "owners")
public class Owner extends User {

    public Owner(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
}
