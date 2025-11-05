package com.softeng.backend.controllers.pet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.dto.DiaryDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.enums.AnimalGroup;
import com.softeng.backend.models.enums.PetSexType;
import com.softeng.backend.models.enums.SterileStatus;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.services.pet.PetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    /***************************************************************************
     * TEST CREATE ENDPOINT
     ***************************************************************************/
    @Test
    public void testCreatePetSuccess() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, date, SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        when(petService.createPet(any(Pet.class))).thenReturn(new PetDTO(MOCK_ID, pet));

        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isOk())
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

    @Test
    public void testCreatePetFailNotFoundOwner() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, date, SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        when(petService.createPet(any(Pet.class))).thenThrow(DocumentNotFoundException.class);

        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreatePetFailInternalServerError() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, date, SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);

        when(petService.createPet(any(Pet.class))).thenThrow(ExecutionException.class);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isInternalServerError());

        when(petService.createPet(any(Pet.class))).thenThrow(InterruptedException.class);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isInternalServerError());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "efw#!@$31"})
    void testCreatePetFailsWhenNameInvalid(String invalidName) throws Exception {
        Pet pet = new Pet(invalidName, "1", "dog", "husky", true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void testCreatePetFailsWhenOwnerIdInvalid(String invalidOwnerId) throws Exception {
        Pet pet = new Pet("Bin", invalidOwnerId, "dog", "husky", true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "efw#!@$31"})
    void testCreatePetFailsWhenSpeciesInvalid(String invalidSpecies) throws Exception {
        Pet pet = new Pet("Bin", "1", invalidSpecies, "husky", true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "efw#!@$31"})
    void testCreatePetFailsWhenBreedInvalid(String invalidBreed) throws Exception {
        Pet pet = new Pet("Bin", "1", "dog", invalidBreed, true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"notBoolean", "efw#!@$31"})
    void testCreatePetFailsWhenEstimatedBirthdateInvalid(String invalidEstimatedBirthdate) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "%s",
            "sex": "FEMALE",
            "birthdate": "2025-01-01",
            "sterileStatus": "STERILE",
            "animalGroup": "SMALL_MAMMAL"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidEstimatedBirthdate);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"notEnum", "efw#!@$31"})
    void testCreatePetFailsWhenSexInvalid(String invalidSex) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "true",
            "sex": "%s",
            "birthdate": "2025-01-01",
            "sterileStatus": "STERILE",
            "animalGroup": "SMALL_MAMMAL"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidSex);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"notDate", "efw#!@$31", "2025-20-12"})
    void testCreatePetFailsWhenBirthdateInvalid(String invalidBirthdate) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "true",
            "sex": "FEMALE",
            "birthdate": "%s",
            "sterileStatus": "STERILE",
            "animalGroup": "SMALL_MAMMAL"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidBirthdate);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"notEnum", "efw#!@$31"})
    void testCreatePetFailsWhenSterileStatusInvalid(String invalidSterileStatus) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "true",
            "sex": "FEMALE",
            "birthdate": "2025-01-01",
            "sterileStatus": "%s",
            "animalGroup": "SMALL_MAMMAL"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidSterileStatus);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"notEnum", "efw#!@$31"})
    void testCreatePetFailsWhenAnimalGroupInvalid(String invalidAnimalGroup) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "true",
            "sex": "FEMALE",
            "birthdate": "2025-01-01",
            "sterileStatus": "STERILE",
            "animalGroup": "%s"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidAnimalGroup);
        this.mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    /***************************************************************************
     * TEST GET ENDPOINT
     ***************************************************************************/
    @Test
    public void testGetPetByIdSuccess() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, date, SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        when(petService.getPetById(anyString())).thenReturn(new PetDTO(MOCK_ID, pet));

        this.mockMvc.perform(get("/api/v1/pets/{petId}", MOCK_ID))
                .andExpect(status().isOk())
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

    @Test
    public void testGetPetByIdNotFound() throws Exception {
        when(petService.getPetById(anyString())).thenThrow(DocumentNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/pets/{petId}", MOCK_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPetByIdInternalServerError() throws Exception {
        when(petService.getPetById(any(String.class))).thenThrow(ExecutionException.class);

        this.mockMvc.perform(get("/api/v1/pets/{petId}", MOCK_ID))
                .andExpect(status().isInternalServerError());

        when(petService.getPetById(any(String.class))).thenThrow(InterruptedException.class);

        this.mockMvc.perform(get("/api/v1/pets/{petId}", MOCK_ID))
                .andExpect(status().isInternalServerError());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  "})
    public void testGetPetByIdBadRequest(String id) throws Exception {
        when(petService.getPetById(anyString())).thenThrow(ExecutionException.class);

        this.mockMvc.perform(get("/api/v1/pets/{petId}",id))
                .andExpect(status().isBadRequest());
    }

    /***************************************************************************
     * TEST UPDATE ENDPOINT
     ***************************************************************************/
    @Test
    public void testUpdatePetSuccess() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, date, SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        when(petService.updatePet(anyString(), any(Pet.class))).thenReturn(new PetDTO(MOCK_ID, pet));

        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isOk())
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

    @Test
    public void testUpdatePetFailNotFound() throws Exception {
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        when(petService.updatePet(anyString(), any(Pet.class))).thenThrow(DocumentNotFoundException.class);

        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdatePetFailInternalServerError() throws Exception {
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);

        when(petService.updatePet(anyString(), any(Pet.class))).thenThrow(ExecutionException.class);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isInternalServerError());

        when(petService.updatePet(anyString(), any(Pet.class))).thenThrow(InterruptedException.class);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isInternalServerError());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  "})
    public void testUpdatePetBadId(String id) throws Exception {
        when(petService.getPetById(anyString())).thenThrow(ExecutionException.class);

        this.mockMvc.perform(put("/api/v1/pets/{petId}",id))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "efw#!@$31"})
    void testUpdatePetFailsWhenNameInvalid(String invalidName) throws Exception {
        Pet pet = new Pet(invalidName, "1", "dog", "husky", true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void testUpdatePetFailsWhenOwnerIdInvalid(String invalidOwnerId) throws Exception {
        Pet pet = new Pet("Bin", invalidOwnerId, "dog", "husky", true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "efw#!@$31"})
    void testUpdatePetFailsWhenSpeciesInvalid(String invalidSpecies) throws Exception {
        Pet pet = new Pet("Bin", "1", invalidSpecies, "husky", true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "efw#!@$31"})
    void testUpdatePetFailsWhenBreedInvalid(String invalidBreed) throws Exception {
        Pet pet = new Pet("Bin", "1", "dog", invalidBreed, true, PetSexType.FEMALE, new Date(), SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"notBoolean", "efw#!@$31"})
    void testUpdatePetFailsWhenEstimatedBirthdateInvalid(String invalidEstimatedBirthdate) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "%s",
            "sex": "FEMALE",
            "birthdate": "2025-01-01",
            "sterileStatus": "STERILE",
            "animalGroup": "SMALL_MAMMAL"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidEstimatedBirthdate);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"notEnum", "efw#!@$31"})
    void testUpdatePetFailsWhenSexInvalid(String invalidSex) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "true",
            "sex": "%s",
            "birthdate": "2025-01-01",
            "sterileStatus": "STERILE",
            "animalGroup": "SMALL_MAMMAL"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidSex);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"notDate", "efw#!@$31", "2025-20-12"})
    void testUpdatePetFailsWhenBirthdateInvalid(String invalidBirthdate) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "true",
            "sex": "FEMALE",
            "birthdate": "%s",
            "sterileStatus": "STERILE",
            "animalGroup": "SMALL_MAMMAL"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidBirthdate);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"notEnum", "efw#!@$31"})
    void testUpdatePetFailsWhenSterileStatusInvalid(String invalidSterileStatus) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "true",
            "sex": "FEMALE",
            "birthdate": "2025-01-01",
            "sterileStatus": "%s",
            "animalGroup": "SMALL_MAMMAL"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidSterileStatus);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"notEnum", "efw#!@$31"})
    void testUpdatePetFailsWhenAnimalGroupInvalid(String invalidAnimalGroup) throws Exception {
        String petJsonTemplate = """
        {
            "name": "Bin",
            "ownerId": "1",
            "species": "dog",
            "breed": "husky",
            "estimatedBirthdate": "true",
            "sex": "FEMALE",
            "birthdate": "2025-01-01",
            "sterileStatus": "STERILE",
            "animalGroup": "%s"
        }
        """;
        String petJson = String.format(petJsonTemplate, invalidAnimalGroup);
        this.mockMvc.perform(put("/api/v1/pets/{petId}", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson))
                .andExpect(status().isBadRequest());
    }

    /***************************************************************************
     * TEST DELETE ENDPOINT
     ***************************************************************************/
    @Test
    public void testDeletePetByIdSuccess() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Bin", "1", "dog", "husky", true, PetSexType.FEMALE, date, SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        when(petService.deletePet(anyString())).thenReturn(new PetDTO(MOCK_ID, pet));

        this.mockMvc.perform(delete("/api/v1/pets/{petId}", MOCK_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeletePetByIdNotFound() throws Exception {
        when(petService.deletePet(anyString())).thenThrow(DocumentNotFoundException.class);

        this.mockMvc.perform(delete("/api/v1/pets/{petId}", MOCK_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePetByIdInternalServerError() throws Exception {
        when(petService.deletePet(any(String.class))).thenThrow(ExecutionException.class);

        this.mockMvc.perform(delete("/api/v1/pets/{petId}", MOCK_ID))
                .andExpect(status().isInternalServerError());

        when(petService.deletePet(any(String.class))).thenThrow(InterruptedException.class);

        this.mockMvc.perform(delete("/api/v1/pets/{petId}", MOCK_ID))
                .andExpect(status().isInternalServerError());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  "})
    public void testDeletePetByIdBadRequest(String id) throws Exception {
        when(petService.deletePet(anyString())).thenThrow(ExecutionException.class);

        this.mockMvc.perform(delete("/api/v1/pets/{petId}",id))
                .andExpect(status().isBadRequest());
    }

    /***************************************************************************
     * TEST CREATE DIARY ENTRY ENDPOINT
     ***************************************************************************/
    @Test
    public void testCreateDiaryEntry() throws Exception {
        Date date = new Date();
        OffsetDateTime odt = date.toInstant().atOffset(ZoneOffset.UTC);
        String isoString = odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        ArrayList<String> files = new ArrayList<>();
        files.add("file1");
        files.add("file2");
        Diary diary = new Diary("diary", "something", files, date);
        when(petService.addDiaryEntry(anyString(), any(Diary.class))).thenReturn(new DiaryDTO(MOCK_ID, diary));

        this.mockMvc.perform(post("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(MOCK_ID))
                .andExpect(jsonPath("$.contentType").value("diary"))
                .andExpect(jsonPath("$.contentBody").value("something"))
                .andExpect(jsonPath("$.files").value(files))
                .andExpect(jsonPath("$.createTimestamp").value(isoString));
    }

    @Test
    public void testCreateDiaryEntryNotFound() throws Exception {
        Date date = new Date();
        ArrayList<String> files = new ArrayList<>();
        files.add("file1");
        files.add("file2");
        Diary diary = new Diary("diary", "something", files, date);
        when(petService.addDiaryEntry(anyString(), any(Diary.class))).thenThrow(DocumentNotFoundException.class);

        this.mockMvc.perform(post("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateDiaryEntryInternalServerError() throws Exception {
        Date date = new Date();
        ArrayList<String> files = new ArrayList<>();
        files.add("file1");
        files.add("file2");
        Diary diary = new Diary("diary", "something", files, date);
        when(petService.addDiaryEntry(anyString(), any(Diary.class))).thenThrow(InterruptedException.class);

        this.mockMvc.perform(post("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isInternalServerError());

        when(petService.addDiaryEntry(anyString(), any(Diary.class))).thenThrow(ExecutionException.class);

        this.mockMvc.perform(post("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isInternalServerError());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void testCreateDiaryEntryInvalidContentType(String invalidContentType) throws Exception {
        Date date = new Date();
        ArrayList<String> files = new ArrayList<>();
        files.add("file1");
        files.add("file2");
        Diary diary = new Diary(invalidContentType, "something", files, date);
        when(petService.addDiaryEntry(anyString(), any(Diary.class))).thenThrow(InterruptedException.class);

        this.mockMvc.perform(post("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void testCreateDiaryEntryInvalidContentBody(String invalidContentBody) throws Exception {
        Date date = new Date();
        ArrayList<String> files = new ArrayList<>();
        files.add("file1");
        files.add("file2");
        Diary diary = new Diary("diary", invalidContentBody, files, date);
        when(petService.addDiaryEntry(anyString(), any(Diary.class))).thenThrow(InterruptedException.class);

        this.mockMvc.perform(post("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateDiaryEntryInvalidFiles() throws Exception {
        Date date = new Date();
        Diary diary = new Diary("diary", "something", null, date);
        when(petService.addDiaryEntry(anyString(), any(Diary.class))).thenThrow(InterruptedException.class);

        this.mockMvc.perform(post("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"9999-12-31T00:00:00Z", "3030-01-01T00:00:00Z"})
    void testCreateDiaryEntryInvalidCreatedTimestamp(String invalidCreatedTimestamp) throws Exception {
        Instant instant = Instant.parse(invalidCreatedTimestamp);
        Date date = Date.from(instant);
        ArrayList<String> files = new ArrayList<>();
        files.add("file1");
        files.add("file2");
        Diary diary = new Diary("diary", invalidCreatedTimestamp, files, date);
        when(petService.addDiaryEntry(anyString(), any(Diary.class))).thenThrow(InterruptedException.class);

        this.mockMvc.perform(post("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isBadRequest());
    }

    /***************************************************************************
     * TEST GET DIARY ENTRY ENDPOINT
     ***************************************************************************/
    @Test
    public void testGetDiaryEntryInRangeSuccess() throws Exception {
        Date from = Date.from(Instant.parse("2025-01-01T00:00:00Z"));
        Date to = Date.from(Instant.parse("2025-12-31T23:59:59Z"));
        int limit = 1000;

        ArrayList<String> file1 = new ArrayList<>();
        file1.add("file1");
        ArrayList<String> file2 = new ArrayList<>();
        file2.add("file2");

        Diary diary1 = new Diary("diary1", "content1", file1, from);
        Diary diary2 = new Diary("diary2", "content2", file2, to);

        List<DiaryDTO> diaryDTOs = List.of(new DiaryDTO("1", diary1), new DiaryDTO("2", diary2));

        when(petService.getDiaryEntryInRange(MOCK_ID, from, to, limit)).thenReturn(diaryDTOs);

        this.mockMvc.perform(get("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .param("from", "2025-01-01T00:00:00Z")
                        .param("to", "2025-12-31T23:59:59Z")
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].contentType").value("diary1"))
                .andExpect(jsonPath("$[0].contentBody").value("content1"))
                .andExpect(jsonPath("$[0].files[0]").value("file1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].contentType").value("diary2"))
                .andExpect(jsonPath("$[1].contentBody").value("content2"))
                .andExpect(jsonPath("$[1].files[0]").value("file2"));
    }

    @Test
    public void testGetDiaryEntryInRangeBadRequestFromAfterTo() throws Exception {
        this.mockMvc.perform(get("/api/v1/pets/{petId}/diaries", MOCK_ID)
                        .param("from", "2025-12-31T23:59:59Z")
                        .param("to", "2025-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetDiaryEntryInRangeNotFound() throws Exception {
        when(petService.getDiaryEntryInRange(anyString(), any(Date.class), any(Date.class), anyInt()))
                .thenThrow(DocumentNotFoundException.class);

        this.mockMvc.perform(get("/api/v1/pets/{petId}/diaries", MOCK_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetDiaryEntryInRangeInternalServerError() throws Exception {
        when(petService.getDiaryEntryInRange(anyString(), any(Date.class), any(Date.class), anyInt()))
                .thenThrow(ExecutionException.class);

        this.mockMvc.perform(get("/api/v1/pets/{petId}/diaries", MOCK_ID))
                .andExpect(status().isInternalServerError());

        when(petService.getDiaryEntryInRange(anyString(), any(Date.class), any(Date.class), anyInt()))
                .thenThrow(InterruptedException.class);

        this.mockMvc.perform(get("/api/v1/pets/{petId}/diaries", MOCK_ID))
                .andExpect(status().isInternalServerError());
    }

}
