package com.softeng.backend.models.pet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PetLite implements IPet {

    @Id
    private String id;
    private String name;
    private String breed;

    @Override
    public boolean isValid() {
        return (name != null && !name.isEmpty()) &&
                (breed != null &&!breed.isEmpty());
    }
}
