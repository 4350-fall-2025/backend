package com.softeng.backend.dto;

import com.softeng.backend.models.pet.Pet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class PetDTO {

    @NotNull @NotBlank
    private final String id;
    @NotNull @NotBlank
    private final Pet pet;

    public PetDTO() {
        this.id = "";
        this.pet = new Pet();
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "name", pet.getName(),
                "ownerId", pet.getOwnerId(),
                "species", pet.getSpecies(),
                "breed", pet.getBreed(),
                "sex", pet.getSex().toString(),
                "birthdate", pet.getBirthdate().toString(),
                "sterileStatus", pet.getSterileStatus().toString(),
                "animalGroup", pet.getAnimalGroup().toString(),
                "estimatedBirthdate", pet.isEstimatedBirthdate()
        );
    }
}
