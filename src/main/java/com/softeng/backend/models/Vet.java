package com.softeng.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import com.google.cloud.spring.data.firestore.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collectionName = "vets")
public class Vet {

    @Id
    private String id;
    private String name;

}