package com.softeng.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.controllers.user.owner.OwnerController;
import com.softeng.backend.controllers.user.vet.VetController;
import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.repository.user.vet.VetRepository;
import com.softeng.backend.services.user.vet.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VetController.class)
@Import({VetService.class})
public class VetIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockitoBean
    private VetRepository vetRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String mockDocId = "mockDocId";

    //TODO: remove when we set up auth
    private static final String mockPass = "MockTokenForNow";
    private static final String mockCert = "Certified";

    @Test
    void testCreateVetCreated() throws Exception {
        Vet vet = new Vet("Minh", "CopyThisTest1", "123@gmail.com", "VerySecure123", mockCert);
        when(vetRepository.createVet(any(Vet.class))).thenReturn(new VetDTO(mockDocId, vet));

        this.mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Minh"))
                .andExpect(jsonPath("$.lastName").value("CopyThisTest1"))
                .andExpect(jsonPath("$.email").value("123@gmail.com"))
                .andExpect(jsonPath("$.certification").value(mockCert))
                .andExpect(jsonPath("$.token").value(mockPass));
    }

    @Test
    void testCreateVetConflict() throws Exception {
        // null DTO ID path:
        Vet vet = new Vet("Minh", "CopyThisTest1", "123@gmail.com", "VerySecure123", mockCert);
        when(vetRepository.createVet(any(Vet.class))).thenReturn(new VetDTO(null, vet));
        this.mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Conflict fields"))
                .andExpect(jsonPath("$.detail.email").value("Email already exists"));

        // null DTO path:
        when(vetRepository.createVet(any(Vet.class))).thenReturn(null);
        this.mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Conflict fields"))
                .andExpect(jsonPath("$.detail.email").value("Email already exists"));
    }

    @Test
    void testCreateVetBadRequest() throws Exception {
        Vet vet = new Vet("Minh", "CopyThisTest1", "   ", "VerySecure123", mockCert);
        when(vetRepository.createVet(any(Vet.class))).thenReturn(null);
        this.mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                // Reference: Copied the following 4 lines from ChatGPT (for convenience)
                .andExpect(jsonPath("$.detail.firstName").value("Name cannot be empty"))
                .andExpect(jsonPath("$.detail.lastName").value("Name cannot be empty"))
                .andExpect(jsonPath("$.detail.email").value("Invalid email format"))
                .andExpect(jsonPath("$.detail.password").value("Must be at least 8 characters"))
                .andExpect(jsonPath("$.detail.certification").value("Certification cannot be empty"));
    }

    @Test
    void testCreateVetServerError() throws Exception {
        Vet vet = new Vet("Minh", "CopyThisTest1", "email@email.com", "VerySecure123", mockCert);
        // this when/thenThrow statement was copied from ChatGPT:
        when(vetRepository.createVet(any(Vet.class)))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore failure")));

        this.mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(vetRepository.createVet(any(Vet.class)))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetVetByIdOk() throws Exception {
        Vet vet = new Vet("Minh", "CopyThisTest1", "123@gmail.com", "VerySecure123", mockCert);

        when(vetRepository.getVetById(mockDocId)).thenReturn(new VetDTO(mockDocId, vet));

        this.mockMvc.perform(get("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Minh"))
                .andExpect(jsonPath("$.lastName").value("CopyThisTest1"))
                .andExpect(jsonPath("$.email").value("123@gmail.com"))
                .andExpect(jsonPath("$.token").value(mockPass))
                .andExpect(jsonPath("$.certification").value(mockCert));
    }

    @Test
    void testGetVetByIdNotFound() throws Exception {
        Vet vet = new Vet("Minh", "CopyThisTest1", "   ", "VerySecure123", mockCert);

        // null id
        when(vetRepository.getVetById(mockDocId)).thenReturn(new VetDTO(null, vet));

        this.mockMvc.perform(get("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // null dto
        when(vetRepository.getVetById(mockDocId)).thenReturn(null);

        this.mockMvc.perform(get("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // empty id
        when(vetRepository.getVetById(any(String.class)))
                .thenReturn(new VetDTO("", vet));
        this.mockMvc.perform(get("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetVetByIdServerError() throws Exception {
        // this when/thenThrow statement was copied from ChatGPT:
        when(vetRepository.getVetById(mockDocId))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(get("/api/v1/vets/{id}", mockDocId))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(vetRepository.getVetById(any(String.class)))
                .thenAnswer(_ -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(get("/api/v1/vets/{id}", mockDocId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateVetOk() throws Exception {
        Vet vet = new Vet("NewName", "", "", "VerySecure123", mockCert);
        when(vetRepository.updateVet(any(String.class), any(Map.class)))
                .thenReturn(new VetDTO(mockDocId, vet));

        this.mockMvc.perform(put("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.lastName").value(""))
                .andExpect(jsonPath("$.email").value(""))
                .andExpect(jsonPath("$.token").value(mockPass))
                .andExpect(jsonPath("$.certification").value(mockCert));
    }

    @Test
    void testUpdateVetBadRequest() throws Exception {
        Vet vet = new Vet("", "", null, null, null);
        when(vetRepository.updateVet(any(String.class), any(Map.class)))
                .thenReturn(new VetDTO(mockDocId, vet));

        this.mockMvc.perform(put("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateVetNotFound() throws Exception {
        Vet vet = new Vet("Minh", "CopyThisTest1", "123@abc.com", "123", mockCert);
        when(vetRepository.updateVet(any(String.class), any(Map.class)))
                .thenReturn(new VetDTO(null, vet));

        this.mockMvc.perform(put("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isNotFound());

        when(vetRepository.updateVet(any(String.class), any(Map.class)))
                .thenReturn(null);

        this.mockMvc.perform(put("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateVetServerError() throws Exception {
        Vet vet = new Vet("Minh", "CopyThisTest1", "email@email.com", "VerySecure123", mockCert);

        // this when/thenThrow statement was copied from ChatGPT:
        when(vetRepository.updateVet(any(String.class), any(Map.class)))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(put("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(vetRepository.updateVet(any(String.class), any(Map.class)))
                .thenAnswer(_ -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(put("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteVetNoContent() throws Exception {
        Vet vet = new Vet("NewName", "NewName", "new@name.com", "VerySecure123", mockCert);
        when(vetRepository.getVetById(any(String.class)))
                .thenReturn(new VetDTO(mockDocId, vet));
        when(vetRepository.deleteVet(any(String.class)))
                .thenReturn(true);

        this.mockMvc.perform(delete("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteVetNotFound() throws Exception {
        // null ID
        Vet vet = new Vet("NewName", "NewName", "new@name.com", "VerySecure123", mockCert);
        when(vetRepository.getVetById(any(String.class)))
                .thenReturn(new VetDTO(null, vet));

        this.mockMvc.perform(delete("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isNotFound());

        // null DTO
        when(vetRepository.getVetById(any(String.class)))
                .thenReturn(null);

        this.mockMvc.perform(delete("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isNotFound());

        // empty id
        when(vetRepository.getVetById(any(String.class)))
                .thenReturn(new VetDTO("", vet));

        this.mockMvc.perform(delete("/api/v1/vets/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vet)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteVetServerError() throws Exception {
        // this when/thenThrow statement was copied from ChatGPT:
        when(vetRepository.getVetById(mockDocId))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(delete("/api/v1/vets/{id}", mockDocId))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(vetRepository.getVetById(any(String.class)))
                .thenAnswer(_ -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(delete("/api/v1/vets/{id}", mockDocId))
                .andExpect(status().isInternalServerError());
    }
}
