package com.softeng.backend.services.user;

import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.repository.user.vet.VetRepository;
import com.softeng.backend.services.user.vet.VetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VetServiceTest {
    @Mock
    private VetRepository vetRepository;

    @InjectMocks
    private VetService vetService;

    private static final Vet vet = new Vet("Victoria", "MadeThisTest1", "123@gmail.com", "VerySecure123", "Certified");
    private static final String mockDocId = "mockDocId";

    @Test
    void createVetShouldWorkWithNewUser() throws Exception {
        when(vetRepository.createVet(vet))
                .thenReturn(new VetDTO(mockDocId, vet));

        when(vetService.getVetByEmail(vet.getEmail()))
                .thenReturn(new VetDTO());

        VetDTO dto = vetService.createVet(vet);
        assertEquals(mockDocId, dto.getId());
        assertEquals(vet, dto.getVet());
    }

    @Test
    void createVetShouldFailWithExistingUser() throws Exception {
        lenient().when(vetRepository.getVetByEmail(vet.getEmail()))
                .thenReturn(new VetDTO(mockDocId, vet));

        lenient().when(vetRepository.createVet(vet))
                .thenReturn(new VetDTO(mockDocId, vet));

        VetDTO dto = vetService.createVet(vet);
        assertEquals("", dto.getId());
    }

    @Test
    void createVetShouldFailWithInvalidInput() throws Exception {
        lenient().when(vetRepository.createVet(null))
                .thenReturn(new VetDTO());

        VetDTO dto = vetService.createVet(null);
        assertEquals("", dto.getId());
    }

    @Test
    void getVetByIdShouldHandleValidId() throws Exception {
        when(vetRepository.getVetById(mockDocId))
                .thenReturn(new VetDTO(mockDocId, vet));

        VetDTO dto = vetService.getVetById(mockDocId);
        assertEquals(mockDocId, dto.getId());
    }

    @Test
    void getVetByIdShouldHandleInvalidId() throws Exception {
        when(vetRepository.getVetById(null))
                .thenReturn(new VetDTO());

        VetDTO dto = vetService.getVetById(null);
        assertEquals("", dto.getId());
    }

    @Test
    void getVetByEmailShouldHandleValidEmail() throws Exception {
        when(vetRepository.getVetByEmail(vet.getEmail()))
                .thenReturn(new VetDTO(mockDocId, vet));

        VetDTO dto = vetService.getVetByEmail(vet.getEmail());
        assertEquals(vet.getEmail(), dto.getVet().getEmail());
    }

    @Test
    void getVetByEmailShouldHandleInvalidEmail() throws Exception {
        when(vetRepository.getVetByEmail(null))
                .thenReturn(new VetDTO());

        VetDTO dto = vetService.getVetByEmail(null);
        assertNotNull(dto);
        assertNotNull(dto.getVet());
        assert(dto.isEmpty());
    }

    /**
     * DUE TO TIME CONSTRAINTS the following updateVet & deleteVet tests were
     * generated using ChatGPT (GPT-5 Mini)
     * But they were vetted and edited by Victoria Iskandar to ensure they
     * actually test correctly.
     */

    @Test
    void updateVetShouldHandleValidData() throws Exception {
        // Arrange
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("firstName", vet.getFirstName());
        updateFields.put("lastName", vet.getLastName());
        updateFields.put("email", vet.getEmail());
        updateFields.put("certification", vet.getCertification());

        VetDTO expectedDTO = new VetDTO(); // set expected fields as needed
        when(vetRepository.updateVet(mockDocId, updateFields)).thenReturn(expectedDTO);

        // Act
        VetDTO result = vetService.updateVet(mockDocId, vet);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result); // compares objects; override equals in DTO if needed
        verify(vetRepository).updateVet(mockDocId, updateFields);
    }

    @Test
    void updateVetShouldHandleEmptyUser() throws Exception {
        // Arrange
        Vet emptyVet = mock(Vet.class);
        when(emptyVet.checkEmptyUser()).thenReturn(true);

        // Act
        VetDTO result = vetService.updateVet(mockDocId, emptyVet);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getId());
        verify(vetRepository, never()).updateVet(anyString(), anyMap());
    }

    @Test
    void updateVetShouldHandlePartialUpdate() throws Exception {
        // Arrange
        Vet partialVet = new Vet(null, "UpdatedLast", null, null, null); // only lastName
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("lastName", partialVet.getLastName());

        VetDTO expectedDTO = new VetDTO(mockDocId, partialVet);
        when(vetRepository.updateVet(mockDocId, updateFields)).thenReturn(expectedDTO);

        // Act
        VetDTO result = vetService.updateVet(mockDocId, partialVet);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        assertEquals("UpdatedLast", result.getVet().getLastName());
        verify(vetRepository).updateVet(mockDocId, updateFields);
    }

    @Test
    void deleteVetShouldHandleValidId() {
        when(vetRepository.deleteVet(mockDocId)).thenReturn(true);

        boolean result = vetService.deleteVet(mockDocId);

        assert(result);
        verify(vetRepository).deleteVet(mockDocId);
    }

    @Test
    void deleteVetShouldHandleInvalidId() {
        when(vetRepository.deleteVet(mockDocId)).thenReturn(false);

        boolean result = vetService.deleteVet(mockDocId);

        assert(!result);
        verify(vetRepository).deleteVet(mockDocId);
    }
}
