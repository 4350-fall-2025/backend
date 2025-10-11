package com.softeng.backend.models.pet;

import com.google.cloud.Date;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Document(collectionName = "pets")
@Data // TODO - need to update this class in a separate ticket instead of using all getters/setters
@NoArgsConstructor
@AllArgsConstructor
public class Pet implements IPet {

    @Id
    private String id;
    private String name;
    private String ownerId;
    private String breed;
    private String sex;
    private String species;
    private Date birthDate;

}
