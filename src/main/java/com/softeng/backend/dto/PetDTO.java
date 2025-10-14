package com.softeng.backend.dto;

import com.google.cloud.Date;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.pet.SterileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PetDTO {

    private String id;
    private String ownerId;
    private String species;
    private String breed;
    private String sex;
    private String birthDate;
    private String sterileStatus;


    public Pet build() {
        return new Pet(id, ownerId, species, breed, sex, birthDate, sterileStatus);
    }

//    public Map<String, Object> toMap() {
//        return Map.of(
//                "id", id,
//                "name", pet.getName(),
//                "ownerId", pet.getOwnerId(),
//                "species", pet.getSpecies(),
//                "breed", pet.getBreed(),
//                "sex", pet.getSex(),
//                "birthDate", pet.getBirthDate().toString(),
//                "sterileStatus", pet.getSterileStatus().toString()
//        );
//    }

}
