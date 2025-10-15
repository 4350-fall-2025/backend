package com.softeng.backend.dto;

import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.user.owner.Owner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

/*
Because firestore handles/auto creates document ids, it was a bit awkward storing an "id" field
in the User object.

The following code was developed with guidance from OpenAI's ChatGPT (https://chat.openai.com)
 - Asked ChatGPT about the weirdness due to firestore having ids automatically, 
    one option it provided was to make a data transfer object, that will be returned
    to the front end clients, and include the document id without coupling it to the 
    User domain object.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OwnerDTO {

    private String id = "";
    private Owner owner;

    public Map<String, Object> toMap() {
        if(owner == null)
            owner = new Owner();

        return Map.of(
                "id", id,
                "firstName", owner.getFirstName(),
                "lastName", owner.getLastName(),
                "email", owner.getEmail(),
                "token", "MockTokenForNow"
        );
    }
}
