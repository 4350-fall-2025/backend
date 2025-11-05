package com.softeng.backend.models.pet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.spring.data.firestore.Document;
import com.softeng.backend.models.enums.AnimalGroup;
import com.softeng.backend.models.enums.PetSexType;
import com.softeng.backend.models.enums.SterileStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Document(collectionName = "pets")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Pet implements IPet {

    @NotNull @NotBlank @Pattern(regexp = "^[A-Za-z\\s\\-]+$", message = "Name must contain only letters, spaces, or hyphens")
    private String name;
    @NotNull @NotBlank
    private String ownerId;
    @NotNull @NotBlank @Pattern(regexp = "^[A-Za-z\\s\\-]+$", message = "species must contain only letters, spaces, or hyphens")
    private String species;
    @NotNull @NotBlank @Pattern(regexp = "^[A-Za-z\\s\\-]+$", message = "breed must contain only letters, spaces, or hyphens")
    private String breed;

    @NotNull
    private boolean estimatedBirthdate;

    @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PetSexType sex;

    @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") @PastOrPresent(message = "Birth date must be on or before today")
    private Date birthdate;

    @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING)
    private SterileStatus sterileStatus;

    @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING)
    private AnimalGroup animalGroup;
}

