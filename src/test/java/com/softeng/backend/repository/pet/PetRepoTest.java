package com.softeng.backend.repository.pet;

import com.google.cloud.firestore.*;
import com.softeng.backend.dto.DiaryDTO;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.models.enums.PetSexType;
import com.softeng.backend.models.enums.SterileStatus;
import com.softeng.backend.models.enums.AnimalGroup;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests written by ChatGPT (GPT-5)
 * Due to time constraints, I asked ChatGPT to make a test similar to our OwnerRepoTest
 * Given the PetRepo file and its functions. Victoria Iskandar reviewed the tests
 * to verify they are correct and test the right things.
 */

@SpringBootTest
@ActiveProfiles("emulator")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PetRepoTest {

    @Autowired
    private Firestore firestore;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    private String ownerId;
    private String petId;

    // Helper to create valid Pet objects
    private Pet validPet(String name) {
        return new Pet(
                name,
                ownerId,
                "Domestic",
                "Short Hair",
                false,
                PetSexType.MALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.SMALL_MAMMAL
        );
    }

    @BeforeAll
    void setup() throws Exception {
        // Create owner
        OwnerDTO ownerDTO = ownerRepository.createOwner(
                new Owner(
                        "Pet", "Owner", "petowner@gmail.com", "StrongPass123!"
                )
        );
        ownerId = ownerDTO.getId();

        // Create pet
        Pet pet = validPet("Sparky");
        PetDTO created = petRepository.createPet(pet);
        petId = created.getId();
    }

    // =============== CREATE PET ===============
    @Test
    void testCreatePetSuccess() throws Exception {
        Pet pet = validPet("Luna");

        PetDTO dto = petRepository.createPet(pet);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals("Luna", dto.getPet().getName());
        assertEquals(ownerId, dto.getPet().getOwnerId());
    }

    @Test
    void testCreatePetOwnerNotFound() {
        Pet pet = new Pet(
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

        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.createPet(pet));
    }

    // =============== GET PET ===============
    @Test
    void testGetPetByIdSuccess() throws Exception {
        PetDTO dto = petRepository.getPetById(petId);
        assertNotNull(dto);
        assertEquals(petId, dto.getId());
    }

    @Test
    void testGetPetByIdNotFound() {
        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.getPetById("invalid"));
    }

    // =============== GET PETS BY OWNER ===============
    @Test
    void testGetPetsByOwnerIdSuccess() throws Exception {
        List<PetDTO> pets = petRepository.getPetsByOwnerId(ownerId);
        assertNotNull(pets);
        assertFalse(pets.isEmpty());
    }

    @Test
    void testGetPetsByOwnerIdOwnerNotFound() {
        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.getPetsByOwnerId("invalidOwner"));
    }

    // =============== UPDATE PET ===============
    @Test
    void testUpdatePetSuccess() throws Exception {
        Pet updated = new Pet(
                "Renamed",
                ownerId,
                "Dragon",
                "Fire Drake",
                false,
                PetSexType.FEMALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.OTHER
        );

        PetDTO dto = petRepository.updatePet(petId, updated);
        assertNotNull(dto);
        assertEquals("Renamed", dto.getPet().getName());
        assertEquals("Dragon", dto.getPet().getSpecies());
    }

    @Test
    void testUpdatePetNotFound() {
        Pet p = validPet("Name");
        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.updatePet("invalidId", p));
    }

    @Test
    void testUpdatePetOwnerNotFound() {
        Pet p = new Pet(
                "Valid",
                "invalidOwner",
                "Cat",
                "Tabby",
                false,
                PetSexType.MALE,
                new Date(),
                SterileStatus.STERILE,
                AnimalGroup.SMALL_MAMMAL
        );

        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.updatePet(petId, p));
    }

    // =============== DELETE PET ===============
    @Test
    void testDeletePetSuccess() throws Exception {
        // Create temporary pet to delete
        Pet temp = validPet("Temp");
        PetDTO created = petRepository.createPet(temp);

        PetDTO deleted = petRepository.deletePet(created.getId());
        assertNotNull(deleted);
        assertEquals(created.getId(), deleted.getId());

        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.getPetById(created.getId()));
    }

    @Test
    void testDeletePetNotFound() {
        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.deletePet("invalid"));
    }

    // =============== ADD DIARY ENTRY ===============
    @Test
    void testAddDiaryEntrySuccess() throws Exception {
        Diary diary = new Diary(
                "NOTE",
                "Annual check-up complete.",
                new ArrayList<>(List.of("file1.png", "file2.png")),
                new Date() // now or earlier is valid
        );

        DiaryDTO dto = petRepository.addDiaryEntry(petId, diary);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals("Annual check-up complete.", dto.getDiary().getContentBody());
    }

    @Test
    void testAddDiaryEntryPetNotFound() {
        Diary diary = new Diary(
                "NOTE",
                "Annual check-up complete.",
                new ArrayList<>(List.of("file1.png", "file2.png")),
                new Date() // now or earlier is valid
        );

        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.addDiaryEntry("invalid", diary));
    }

    // =============== GET DIARY IN RANGE ===============
    @Test
    void testGetDiaryEntryInRange() throws Exception {
        Date now = new Date();
        Date earlier = new Date(now.getTime() - 10000);
        Date later = new Date(now.getTime() + 10000);

        List<DiaryDTO> entries = petRepository.getDiaryEntryInRange(
                petId,
                earlier,
                later,
                10
        );

        assertNotNull(entries);
    }

    @Test
    void testGetDiaryEntryRangePetNotFound() {
        Date d = new Date();
        assertThrows(DocumentNotFoundException.class,
                () -> petRepository.getDiaryEntryInRange("invalid", d, d, 10));
    }

    @AfterAll
    void tearDown() throws Exception {
        petRepository.deletePet(petId);
        ownerRepository.deleteOwner(ownerId);
    }
}
