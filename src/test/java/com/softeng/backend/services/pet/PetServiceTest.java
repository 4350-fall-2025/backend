package com.softeng.backend.services.pet;

import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.diary.Diary;
import com.softeng.backend.models.enums.AnimalGroup;
import com.softeng.backend.models.enums.PetSexType;
import com.softeng.backend.models.enums.SterileStatus;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.repository.pet.PetRepository;
import com.softeng.backend.services.user.owner.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private PetService petService;

    private static final String MOCK_OWNER_ID = "mockDocId";
    private static final String MOCK_ID = "mockId";

    //private static final Pet pet = new Pet("Basma","1","human", "husky", FALSE, PetSexType.FEMALE,new Date());

    // =========================
    // CREATE
    // =========================
    @Test
    public void createPetTestSuccess() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Basma",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        when(petRepository.createPet(any(Pet.class))).thenReturn(new PetDTO(MOCK_ID, pet));

        PetDTO result = petService.createPet(pet);
        verify(petRepository).createPet(pet);
        assertEquals(MOCK_ID, result.getId());
        assertEquals(pet,result.getPet());
    }

    @Test
    public void createPetTestInvalidOwner() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Basma",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        when(petRepository.createPet(any(Pet.class)))
                .thenThrow(new DocumentNotFoundException(("Owner not found")));

        DocumentNotFoundException thrown = assertThrows(
                DocumentNotFoundException.class,
                () -> petService.createPet(pet)
        );
        assertEquals("Owner not found", thrown.getMessage());
    }

    @Test
    public void getPetByIdTestSuccess() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Basma",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        when(petRepository.getPetById(MOCK_ID)).thenReturn(new PetDTO(MOCK_ID, pet));

        PetDTO result = petService.getPetById(MOCK_ID);
        verify(petRepository).getPetById(MOCK_ID);
        assertEquals(MOCK_ID, result.getId());
        assertEquals(pet,result.getPet());
    }

    @Test
    public void getPetByIdTestFailure() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Basma",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        when(petRepository.getPetById(MOCK_ID)).thenThrow(new DocumentNotFoundException(("Pet not found")));

        DocumentNotFoundException thrown = assertThrows(
                DocumentNotFoundException.class,
                () -> petService.getPetById(MOCK_ID)
        );
        assertEquals("Pet not found", thrown.getMessage());
    }

    @Test
    public void getPetsByOwnerIdTestSuccess() throws Exception {
        Date date = new Date();
        Pet pet1 = new Pet("Basma",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        Pet pet2 = new Pet("Basma2",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        when(petRepository.getPetsByOwnerId(MOCK_OWNER_ID))
                .thenReturn(Arrays.asList(
                        new PetDTO("pet1Id", pet1),
                        new PetDTO("pet2Id", pet2)));

        List<PetDTO> result = petService.getPetsByOwnerId(MOCK_OWNER_ID);
        verify(petRepository).getPetsByOwnerId(MOCK_OWNER_ID);
        assertEquals(2, result.size());
        assertEquals(pet1,result.get(0).getPet());
        assertEquals("pet1Id",result.get(0).getId());
        assertEquals(pet2,result.get(1).getPet());
        assertEquals("pet2Id",result.get(1).getId());
    }

    @Test
    public void getPetsByOwnerIdTestInvalidOwner() throws Exception {
        when(petRepository.getPetsByOwnerId(MOCK_OWNER_ID))
                .thenThrow(new DocumentNotFoundException("Owner not found"));

        DocumentNotFoundException thrown = assertThrows(
                DocumentNotFoundException.class,
                () -> petService.getPetsByOwnerId(MOCK_OWNER_ID)
        );

        assertEquals("Owner not found", thrown.getMessage());
    }

    @Test
    public void updatePetTestSuccess() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Basma",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        when(petRepository.updatePet(MOCK_ID, pet)).thenReturn(new  PetDTO(MOCK_ID, pet));

        PetDTO result = petService.updatePet(MOCK_ID, pet);
        verify(petRepository).updatePet(MOCK_ID, pet);
        assertEquals(MOCK_ID, result.getId());
        assertEquals(pet,result.getPet());
    }

    @Test
    public void updatePetTestPetNotFound() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Basma",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        when(petRepository.updatePet(MOCK_ID, pet)).thenThrow(new DocumentNotFoundException("Pet not found"));

        DocumentNotFoundException thrown = assertThrows(
                DocumentNotFoundException.class,
                () -> petService.updatePet(MOCK_ID, pet)
        );

        assertEquals("Pet not found", thrown.getMessage());
        verify(ownerService, never()).updatePet(anyString(), any(PetLite.class));
    }

    @Test
    public void updatePetTestOwnerNotFound() throws Exception {
        Date date = new Date();
        Pet pet = new Pet("Basma",MOCK_OWNER_ID,"dog", "husky", false, PetSexType.FEMALE,date, SterileStatus.NON_STERILE, AnimalGroup.AMPHIBIAN);
        when(petRepository.updatePet(MOCK_ID, pet)).thenReturn(new PetDTO(MOCK_ID, pet));
        doThrow(new DocumentNotFoundException("Owner not found"))
                .when(ownerService)
                .updatePet(eq(MOCK_OWNER_ID), any(PetLite.class));

        DocumentNotFoundException thrown = assertThrows(
                DocumentNotFoundException.class,
                () -> petService.updatePet(MOCK_ID, pet)
        );

        assertEquals("Owner not found", thrown.getMessage());
    }

//    @Test
//    public void addDiaryEntryTestSuccess() throws Exception {
//        Diary diary = new Diary();
//    }



}
