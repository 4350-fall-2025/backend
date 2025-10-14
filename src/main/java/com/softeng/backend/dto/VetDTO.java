package com.softeng.backend.dto;

import com.softeng.backend.models.user.vet.Vet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VetDTO {
    private String id;
    private Vet vet;

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
}
