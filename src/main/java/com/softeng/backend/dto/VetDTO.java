package com.softeng.backend.dto;

import com.softeng.backend.models.user.vet.Vet;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
public class VetDTO {
    private final String id;
    private final Vet vet;

    public VetDTO(String id, Vet vet) {
        this.id = Objects.requireNonNullElse(id, "");
        this.vet = Objects.requireNonNullElse(vet, new Vet());
    }

    public VetDTO() {
        this.id = "";
        this.vet = new Vet();
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "firstName", vet.getFirstName(),
                "lastName", vet.getLastName(),
                "email", vet.getEmail(),
                "certification", vet.getCertification(),
                "token", "MockTokenForNow"
        );
    }

    public boolean isEmpty() {
        return id.isBlank() || vet.checkEmptyUser();
    }
}
