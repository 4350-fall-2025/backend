package com.softeng.backend.dto;

import com.softeng.backend.models.user.owner.Owner;
import lombok.Getter;
import java.util.Map;
import java.util.Objects;

/*
Because firestore handles/auto creates document ids, it was a bit awkward storing an "id" field
in the User object.

The following code was developed with guidance from OpenAI's ChatGPT (https://chat.openai.com)
 - Asked ChatGPT about the weirdness due to firestore having ids automatically, 
    one option it provided was to make a data transfer object, that will be returned
    to the front end clients, and include the document id without coupling it to the 
    User domain object.
 */
@Getter
public class OwnerDTO {

    private final String id;
    private final Owner owner;

    public OwnerDTO(String id, Owner owner) {
        this.id = Objects.requireNonNullElse(id, "");
        this.owner = Objects.requireNonNullElse(owner, new Owner());
    }

    public OwnerDTO() {
        this.id = "";
        this.owner = new Owner();
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "firstName", owner.getFirstName(),
                "lastName", owner.getLastName(),
                "email", owner.getEmail(),
                "token", "MockTokenForNow"
        );
    }

    public boolean isEmpty() {
        return id.isBlank() || owner.checkEmptyUser();
    }
}
