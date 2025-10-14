package com.softeng.backend.models.pet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.spring.data.firestore.Document;
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
    private String sex;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private SterileStatus sterileStatus;
}
