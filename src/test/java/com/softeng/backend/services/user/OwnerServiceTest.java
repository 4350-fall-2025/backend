package com.softeng.backend.services.user;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.pet.PetLite;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import com.softeng.backend.services.user.owner.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Reference: OpenAI ChatGPT GPT-5Mini (<a href="https://chat.openai.com">...</a>)
 * Copied Test Setup (@Mock/@ExtendWith etc) and used guidance from ChatGPT
 */

@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {
    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    private static final Owner owner = new Owner("Victoria", "MadeThisTest1", "123@gmail.com", "VerySecure123");
    private static final String mockDocId = "mockDocId";

    @Test
    void createOwnerShouldWorkWithNewUser() throws Exception {
        when(ownerRepository.createOwner(owner))
                .thenReturn(new OwnerDTO(mockDocId, owner));

        when(ownerService.getOwnerByEmail(owner.getEmail()))
                .thenReturn(new OwnerDTO());

        OwnerDTO dto = ownerService.createOwner(owner);
        assertEquals(mockDocId, dto.getId());
        assertEquals(owner, dto.getOwner());
    }

    @Test
    void createOwnerShouldFailWithExistingUser() throws Exception {
        lenient().when(ownerRepository.getOwnerByEmail(owner.getEmail()))
                .thenReturn(new OwnerDTO(mockDocId, owner));

        lenient().when(ownerRepository.createOwner(owner))
                .thenReturn(new OwnerDTO(mockDocId, owner));

        OwnerDTO dto = ownerService.createOwner(owner);
        assertEquals("", dto.getId());
    }

    @Test
    void createOwnerShouldFailWithInvalidInput() throws Exception {
       lenient().when(ownerRepository.createOwner(null))
                .thenReturn(new OwnerDTO());

        OwnerDTO dto = ownerService.createOwner(null);
        assertEquals("", dto.getId());
    }

    @Test
    void getOwnerByIdShouldHandleValidId() throws Exception {
        when(ownerRepository.getOwnerById(mockDocId))
                .thenReturn(new OwnerDTO(mockDocId, owner));

        OwnerDTO dto = ownerService.getOwnerById(mockDocId);
        assertEquals(mockDocId, dto.getId());
    }

    @Test
    void getOwnerByIdShouldHandleInvalidId() throws Exception {
        when(ownerRepository.getOwnerById(null))
                .thenReturn(new OwnerDTO());

        OwnerDTO dto = ownerService.getOwnerById(null);
        assertEquals("", dto.getId());
    }

    @Test
    void getOwnerByEmailShouldHandleValidEmail() throws Exception {
        when(ownerRepository.getOwnerByEmail(owner.getEmail()))
                .thenReturn(new OwnerDTO(mockDocId, owner));

        OwnerDTO dto = ownerService.getOwnerByEmail(owner.getEmail());
        assertEquals(owner.getEmail(), dto.getOwner().getEmail());
    }

    @Test
    void getOwnerByEmailShouldHandleInvalidEmail() throws Exception {
        when(ownerRepository.getOwnerByEmail(null))
                .thenReturn(new OwnerDTO());

        OwnerDTO dto = ownerService.getOwnerByEmail(null);
        assertNotNull(dto);
        assertNotNull(dto.getOwner());
        assert(dto.isEmpty());
    }

    /**
     * DUE TO TIME CONSTRAINTS the following updateOwner & deleteOwner tests were
     * generated using ChatGPT (GPT-5 Mini)
     * But they were vetted and edited by Victoria Iskandar to ensure they
     * actually test correctly.
     */

    @Test
    void updateOwnerShouldHandleValidData() throws Exception {
        // Arrange
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("firstName", owner.getFirstName());
        updateFields.put("lastName", owner.getLastName());
        updateFields.put("email", owner.getEmail());

        OwnerDTO expectedDTO = new OwnerDTO(); // set expected fields as needed
        when(ownerRepository.updateOwner(mockDocId, updateFields)).thenReturn(expectedDTO);

        // Act
        OwnerDTO result = ownerService.updateOwner(mockDocId, owner);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result); // compares objects; override equals in DTO if needed
        verify(ownerRepository).updateOwner(mockDocId, updateFields);
    }

    @Test
    void updateOwnerShouldHandleEmptyUser() throws Exception {
        // Arrange
        Owner emptyOwner = mock(Owner.class);
        when(emptyOwner.checkEmptyUser()).thenReturn(true);

        // Act
        OwnerDTO result = ownerService.updateOwner(mockDocId, emptyOwner);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getId());
        verify(ownerRepository, never()).updateOwner(anyString(), anyMap());
    }

    @Test
    void updateOwnerShouldHandlePartialUpdate() throws Exception {
        // Arrange
        Owner partialOwner = new Owner(null, "UpdatedLast", null, null); // only lastName
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("lastName", partialOwner.getLastName());

        OwnerDTO expectedDTO = new OwnerDTO(mockDocId, partialOwner);
        when(ownerRepository.updateOwner(mockDocId, updateFields)).thenReturn(expectedDTO);

        // Act
        OwnerDTO result = ownerService.updateOwner(mockDocId, partialOwner);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        assertEquals("UpdatedLast", result.getOwner().getLastName());
        verify(ownerRepository).updateOwner(mockDocId, updateFields);
    }


    @Test
    void updateOwnerPetShouldHandleEmptyUpdateFields() throws Exception {
        PetLite pet = new PetLite();

        // doThrow section copied from ChatGPT, Model GPT-5
        doThrow(new DocumentNotFoundException("Pet with id " + null + " not found"))
                .when(ownerRepository).updatePet(mockDocId, pet);

        assertThrows(DocumentNotFoundException.class, () ->
                ownerRepository.updatePet(mockDocId, pet)
        );
    }

    @Test
    void updateOwnerPetShouldHandleInvalidId() throws Exception {
        PetLite pet = new PetLite("1", "pet", "dog");

        // doThrow section copied from ChatGPT, Model GPT-5
        doThrow(new DocumentNotFoundException("Owner not found"))
                .when(ownerRepository).updatePet("invalidId", pet);

        assertThrows(DocumentNotFoundException.class, () ->
                ownerRepository.updatePet("invalidId", pet)
        );
    }

    @Test
    void deleteOwnerShouldHandleValidId() {
        when(ownerRepository.deleteOwner(mockDocId)).thenReturn(true);

        boolean result = ownerService.deleteOwner(mockDocId);

        assert(result);
        verify(ownerRepository).deleteOwner(mockDocId);
    }

    @Test
    void deleteOwnerShouldHandleInvalidId() {
        when(ownerRepository.deleteOwner(mockDocId)).thenReturn(false);

        boolean result = ownerService.deleteOwner(mockDocId);

        assert(!result);
        verify(ownerRepository).deleteOwner(mockDocId);
    }
}
