package com.softeng.backend.models.pet;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PetLite implements IPet {

    @Id @NotNull @NotEmpty
    private String id;
    @NotNull @NotEmpty @Pattern(regexp = "^[A-Za-z\\s\\-]+$", message = "Name must contain only letters, spaces, or hyphens")
    private String name;
    @NotNull @NotEmpty @Pattern(regexp = "^[A-Za-z\\s\\-]+$", message = "breed must contain only letters, spaces, or hyphens")
    private String breed;
}
