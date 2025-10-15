package com.softeng.backend.dto;

import com.softeng.backend.models.pet.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PetDTO {

    private String id;
    private Pet pet;

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "name", pet.getName(),
                "ownerId", pet.getOwnerId(),
                "species", pet.getSpecies(),
                "breed", pet.getBreed(),
                "sex", pet.getSex().toString(),
                "birthDate", pet.getBirthdate().toString(),
                "sterileStatus", pet.getSterileStatus().toString()
        );
    }

}
