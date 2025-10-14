package com.softeng.backend.dto;

import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.pet.PetLite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PetLiteDTO {

    private String id;
    private PetLite petLite;

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "name", petLite.getName(),
                "ownerId", petLite.getOwnerId(),
                "species", petLite.getSpecies()
        );
    }

}