package com.softeng.backend.repository.user;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests written by Victoria Iskandar, with minimal help from ChatGPT, Model GPT-5.
 * Left comments in-line when I referred to ChatGPT for help.
 */

@SpringBootTest
@ActiveProfiles("emulator")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OwnerRepoTest {

    @Autowired
    private Firestore firestore;
    @Autowired
    private OwnerRepository ownerRepository;
    private static Owner ownerToUpdate = new Owner("Always", "Changing", "ichange@gmail.com", "StrongPass123!");
    private static Owner ownerWithPets = new Owner("Mario", "Luigi", "itsame@gmail.com", "StrongPass123!");
    private static Owner ownerWithoutPets= new Owner("Princess", "Peach", "peach@gmail.com", "StrongPass123!");
    private String ownerToUpdateId;
    private String ownerWithPetsId;
    private String ownerWithoutPetsId;

    PetLite pet1 = new PetLite("1E", "Sparky", "Siamese");
    PetLite pet2 = new PetLite("12", "Kelsea", "Border Collie");
    PetLite pet3 = new PetLite("1F", "Drogon", "Dragon");

    @BeforeAll
    void setupOwner() throws Exception {
        ownerToUpdateId = ownerRepository.createOwner(ownerToUpdate).getId();
        ownerWithPetsId = ownerRepository.createOwner(ownerWithPets).getId();
        ownerWithoutPetsId = ownerRepository.createOwner(ownerWithoutPets).getId();

        ownerRepository.addPet(ownerWithPetsId, pet2);
        ownerRepository.addPet(ownerWithPetsId, pet3);
    }

    @Test
    void testUpdateOwnerFullUpdate() throws Exception {
        Map<String, Object> updateFields = Map.of(
                "firstName", "Joe",
                "email", "joe@gmail.com",
                "lastName", "Bowie",
                "password", "weshouldneverstorepasswordslikethis"
        );

        ownerToUpdate = new Owner("Joe", "Bowie", "joe@gmail.com", "weshouldneverstorepasswordslikethis");

        OwnerDTO dto = ownerRepository.updateOwner(ownerToUpdateId, updateFields);
        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(ownerToUpdateId, dto.getId());
        assertEquals("Joe", dto.getOwner().getFirstName());
        assertEquals("Bowie", dto.getOwner().getLastName());
        assertEquals("weshouldneverstorepasswordslikethis", dto.getOwner().getPassword());
        assertEquals("joe@gmail.com", dto.getOwner().getEmail());

    }

    @Test
    void testUpdateOwnerPartialUpdate() throws Exception {
        Map<String, Object> updateFields = Map.of(
                "password", "maybehackerswontcrackthisone"
            );

        OwnerDTO dto = ownerRepository.updateOwner(ownerToUpdateId, updateFields);
        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(ownerToUpdateId, dto.getId());
        assertEquals(ownerToUpdate.getEmail(), dto.getOwner().getEmail());
        assertEquals(ownerToUpdate.getFirstName(), dto.getOwner().getFirstName());
        assertEquals(ownerToUpdate.getLastName(), dto.getOwner().getLastName());
        assertEquals("maybehackerswontcrackthisone", dto.getOwner().getPassword());
    }

    @Test
    void testUpdateOwnerInvalidId() throws Exception {
        Map<String, Object> updateFields = Map.of(
                "firstName", "Please",
                "email", "update@gmail.com",
                "lastName", "your",
                "password", "password"
        );

        OwnerDTO dto = ownerRepository.updateOwner("invalid", updateFields);
        assertNotNull(dto);
        assert(dto.getId().isBlank());
    }

    @Test
    void testUpdateOwnerEmptyUpdate() throws Exception {
        Map<String, Object> updateFields = Map.of();

        // assertThrows syntax copied from ChatGPT, Model GPT-5
        assertThrows(IllegalArgumentException.class,
                () -> ownerRepository.updateOwner(ownerToUpdateId, updateFields));
    }

    @Test
    void testUpdateOwnerFullUpdatePet() throws Exception {
        PetLite pet = new PetLite("12", "Arya", "Domestic Shorthair");
        ownerRepository.updatePet(ownerWithPetsId, pet);
        DocumentReference docRef = firestore.collection("owners").document(ownerWithPetsId);
        DocumentSnapshot snapshot = docRef.get().get();
        List<Map<String, Object>> pets = (List<Map<String, Object>>) snapshot.get("pets");
        assertNotNull(pets);
        assertEquals(2, pets.size());
        assert(pets.get(0).get("name").equals("Arya") || pets.get(1).get("name").equals("Arya"));
    }

    @Test
    void testUpdateOwnerPetButPetIsNotInList() throws Exception {
        // assertThrows syntax copied from ChatGPT, Model GPT-5
        assertThrows(DocumentNotFoundException.class, ()->ownerRepository.updatePet(ownerWithPetsId, pet1));
    }

    @Test
    void testUpdateOwnerPetButOwnerHasNoPets() throws Exception {
        // assertThrows syntax copied from ChatGPT, Model GPT-5
        assertThrows(DocumentNotFoundException.class, ()->ownerRepository.updatePet(ownerWithoutPetsId, pet1));
    }

    @AfterAll
    void tearDown() throws Exception {
        ownerRepository.deleteOwner(ownerToUpdateId);
    }
}
