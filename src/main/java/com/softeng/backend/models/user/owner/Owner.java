package com.softeng.backend.models.user.owner;

import com.google.cloud.spring.data.firestore.Document;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.models.user.User;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@Document(collectionName = "owners")
public class Owner extends User implements IOwner {

    public Owner(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }

    public Owner(String firstName, String lastName, String email, String password, ArrayList<PetLite> pets) {
        super(firstName, lastName, email, password);
    }
}
