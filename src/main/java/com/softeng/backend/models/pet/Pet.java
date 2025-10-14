package com.softeng.backend.models.pet;

import com.google.cloud.Date;
import com.google.cloud.spring.data.firestore.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Document(collectionName = "pets")
@Getter
@NoArgsConstructor
public class Pet implements IPet {

    @Id
    private String id;
    private String name;

    @Setter
    private String ownerId;
    private String species;
    private String breed;
    private String sex;
    private Date birthDate;
    private SterileStatus SterileStatus;

    public Pet(String name, String ownerId, String species, String breed,
               String sex, String birthDate, String sterileStatus) {
        this.name = name;
        this.ownerId = ownerId;
        this.species = species;
        this.breed = breed;
        this.sex = sex;
        this.birthDate = Date.parseDate(birthDate);
        this.SterileStatus = SterileStatus.valueOf(sterileStatus);
    }
}
