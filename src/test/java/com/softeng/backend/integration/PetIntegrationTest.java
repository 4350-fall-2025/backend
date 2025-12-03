package com.softeng.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.enums.PetSexType;
import com.softeng.backend.models.enums.SterileStatus;
import com.softeng.backend.models.enums.AnimalGroup;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.pet.PetRepository;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *  Tests written by ChatGPT (GPT-5)
 *  Due to time constraints, I asked ChatGPT to make a test similar to our OwnerIntegrationTest
 *  Given the PetController file and its functions. Victoria Iskandar reviewed the tests
 *  to verify they are correct and test the right things.
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("emulator")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    private String ownerId;
    private String petId;
    private Owner owner;

    @BeforeAll
    void setup() throws Exception {
        // create an owner
        owner = new Owner("Pet", "Owner", "petowner@gmail.com", "StrongPass123!");
        ownerId = ownerRepository.createOwner(owner).getId();

        // create a pet for tests
        Pet pet = new Pet(
                "Sparky",
                ownerId,
                "Dog",
                "Beagle",
                false,
                PetSexType.MALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.SMALL_MAMMAL
        );
        petId = petRepository.createPet(pet).getId();
    }

    // ================= CREATE PET =================
    @Test
    void testCreatePetOk() throws Exception {
        Pet newPet = new Pet(
                "Luna",
                ownerId,
                "Cat",
                "Siamese",
                false,
                PetSexType.FEMALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.SMALL_MAMMAL
        );

        mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Luna"))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.species").value("Cat"))
                .andExpect(jsonPath("$.breed").value("Siamese"));
    }

    @Test
    void testCreatePetOwnerNotFound() throws Exception {
        Pet invalidPet = new Pet(
                "Ghost",
                "invalidOwner",
                "Wolf",
                "Arctic",
                false,
                PetSexType.UNKNOWN,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.OTHER
        );

        mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPet)))
                .andExpect(status().isNotFound());
    }

    // ================= GET PET =================
    @Test
    void testGetPetByIdOk() throws Exception {
        mockMvc.perform(get("/api/v1/pets/{id}", petId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(petId))
                .andExpect(jsonPath("$.name").value("Sparky"));
    }

    @Test
    void testGetPetByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/pets/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ================= UPDATE PET =================
    @Test
    void testUpdatePetOk() throws Exception {
        Owner tempOwner = new Owner(
                "Update",
                "Owner",
                "updateowner+" + System.currentTimeMillis() + "@gmail.com",
                "StrongPass123!"
        );
        String tempOwnerId = ownerRepository.createOwner(tempOwner).getId();

        Pet tempPet = new Pet(
                "Sparky",
                tempOwnerId,
                "Dog",
                "Beagle",
                false,
                PetSexType.MALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.SMALL_MAMMAL
        );
        String tempPetId = petRepository.createPet(tempPet).getId();


        ownerRepository.addPet(tempOwnerId, new PetLite(tempPetId, tempPet.getName(), tempPet.getSpecies()));

        Pet updatedPet = new Pet(
                "Max",
                tempOwnerId,
                "Dog",
                "Beagle",
                false,
                PetSexType.MALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.SMALL_MAMMAL
        );

        mockMvc.perform(put("/api/v1/pets/{id}", tempPetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tempPetId))
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.ownerId").value(tempOwnerId))
                .andExpect(jsonPath("$.sterileStatus").value("STERILE"));

        petRepository.deletePet(tempPetId);
        ownerRepository.deleteOwner(tempOwnerId);
    }



    @Test
    void testUpdatePetNotFound() throws Exception {
        Pet updatedPet = new Pet(
                "Max",
                ownerId,
                "Dog",
                "Beagle",
                false,
                PetSexType.MALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.SMALL_MAMMAL
        );

        mockMvc.perform(put("/api/v1/pets/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPet)))
                .andExpect(status().isNotFound());
    }

    // ================= DELETE PET =================
    @Test
    void testDeletePetNoContent() throws Exception {

        Owner tempOwner = new Owner(
                "Delete",
                "Owner",
                "deleteowner+" + System.currentTimeMillis() + "@gmail.com",
                "StrongPass123!"
        );
        String tempOwnerId = ownerRepository.createOwner(tempOwner).getId();

        Pet tempPet = new Pet(
                "Buddy",
                tempOwnerId,
                "Dog",
                "Beagle",
                false,
                PetSexType.MALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.SMALL_MAMMAL
        );
        String tempPetId = petRepository.createPet(tempPet).getId();

        ownerRepository.addPet(tempOwnerId, new PetLite(tempPetId, tempPet.getName(), tempPet.getSpecies()));
        mockMvc.perform(delete("/api/v1/pets/{id}", tempPetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/pets/{id}", tempPetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        ownerRepository.deleteOwner(tempOwnerId);
    }

    @Test
    void testDeletePetNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/pets/{id}", "non-existent-id"))
                .andExpect(status().isNotFound());
    }

    // ================= ADD DIARY ENTRY =================
    @Test
    void testAddDiaryEntryOk() throws Exception {
        Diary diary = new Diary(
                "NOTE",
                "Vet visit completed.",
                new ArrayList<>(),
                new Date()
        );

        mockMvc.perform(post("/api/v1/pets/{id}/diaries", petId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentBody").value("Vet visit completed."))
                .andExpect(jsonPath("$.contentType").value("NOTE"));
    }

    @Test
    void testAddDiaryEntryPetNotFound() throws Exception {
        Diary diary = new Diary(
                "NOTE",
                "Some note",
                new ArrayList<>(),
                new Date()
        );

        mockMvc.perform(post("/api/v1/pets/{id}/diaries", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diary)))
                .andExpect(status().isNotFound());
    }

    @AfterAll
    void tearDown() throws Exception {
        petRepository.deletePet(petId);
        ownerRepository.deleteOwner(ownerId);
    }
}
