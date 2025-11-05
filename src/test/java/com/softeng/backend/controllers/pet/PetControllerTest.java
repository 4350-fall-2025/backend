package com.softeng.backend.controllers.pet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.models.enums.AnimalGroup;
import com.softeng.backend.models.enums.PetSexType;
import com.softeng.backend.models.enums.SterileStatus;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.services.pet.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = PetController.class)
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetService petService;

    private static final String MOCK_ID = "mockDocId";

    @Test
    public void testCreatePetSuccess() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, date, SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        when(petService.createPet(any(Pet.class))).thenReturn(new PetDTO(MOCK_ID, pet));

        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(MOCK_ID))
                .andExpect(jsonPath("$.name").value("Bin"))
                .andExpect(jsonPath("$.ownerId").value("1"))
                .andExpect(jsonPath("$.species").value("dog"))
                .andExpect(jsonPath("$.breed").value("husky"))
                .andExpect(jsonPath("$.estimatedBirthdate").value("true"))
                .andExpect(jsonPath("$.sex").value("FEMALE"))
                .andExpect(jsonPath("$.birthdate").value(date.toString()))
                .andExpect(jsonPath("$.sterileStatus").value("STERILE"))
                .andExpect(jsonPath("$.animalGroup").value("SMALL_MAMMAL"));
    }
}
