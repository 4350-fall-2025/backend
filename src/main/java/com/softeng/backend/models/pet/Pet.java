package com.softeng.backend.models.pet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.spring.data.firestore.Document;
import com.softeng.backend.models.enums.PetSexType;
import com.softeng.backend.models.enums.SterileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Document(collectionName = "pets")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Pet implements IPet {

    @Id @Setter
    private String id;
    private String name;

    @Setter
    private String ownerId;
    private String species;
    private String breed;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PetSexType sex;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private SterileStatus sterileStatus;

    /**
     * Validators generated with GPT-4.1
     */
    @Override
    public boolean isValid() {

        return isValidName() && isValidSpecies() && isValidBreed() &&
                (sex != null) && isValidBirthDate() && (sterileStatus != null);
    }

    private boolean isValidName() {
        return name != null && !name.trim().isEmpty() && name.length() <= 32 &&
               name.matches("^[A-Za-z\\s\\-]+$");
    }

    private boolean isValidSpecies() {
        return species != null && !species.trim().isEmpty() &&
               species.length() <= 20 && species.matches("^[A-Za-z\\s]+$");
    }

    private boolean isValidBreed() {
        return breed != null && !breed.trim().isEmpty() &&
               breed.length() <= 32 && breed.matches("^[A-Za-z\\s\\-]+$");
    }

    private boolean isValidBirthDate() {
        return birthDate != null && birthDate.before(new Date());
    }
}

