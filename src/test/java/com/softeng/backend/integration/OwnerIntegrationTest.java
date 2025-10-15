package com.softeng.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.controllers.user.owner.OwnerController;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import com.softeng.backend.services.user.owner.OwnerService;
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

@WebMvcTest(controllers = OwnerController.class)
@Import({OwnerService.class})
public class OwnerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerRepository ownerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String mockDocId = "mockDocId";

    //TODO: remove when we set up auth
    private static final String mockToken = "MockTokenForNow";

    @Test
    void testCreateOwnerCreated() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "123@gmail.com", "VerySecure123");
        when(ownerRepository.createOwner(any(Owner.class))).thenReturn(new OwnerDTO(mockDocId, owner));

        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Victoria"))
                .andExpect(jsonPath("$.lastName").value("MadeThisTest1"))
                .andExpect(jsonPath("$.email").value("123@gmail.com"))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testCreateOwnerConflict() throws Exception {

        // null DTO ID path:
        Owner owner = new Owner("Victoria", "MadeThisTest1", "123@gmail.com", "VerySecure123");
        when(ownerRepository.createOwner(any(Owner.class))).thenReturn(new OwnerDTO(null, owner));
        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Conflict fields"))
                .andExpect(jsonPath("$.detail.email").value("Email already exists"));

        // null DTO path:
        when(ownerRepository.createOwner(any(Owner.class))).thenReturn(null);
        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Conflict fields"))
                .andExpect(jsonPath("$.detail.email").value("Email already exists"));
    }

    @Test
    void testCreateOwnerBadRequest() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "   ", "VerySecure123");
        when(ownerRepository.createOwner(any(Owner.class))).thenReturn(null);
        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                // Reference: Copied the following 4 lines from ChatGPT (for convenience)
                .andExpect(jsonPath("$.detail.firstName").value("Name cannot be empty"))
                .andExpect(jsonPath("$.detail.lastName").value("Name cannot be empty"))
                .andExpect(jsonPath("$.detail.email").value("Invalid email format"))
                .andExpect(jsonPath("$.detail.password").value("Must be at least 8 characters"));
    }

    @Test
    void testCreateOwnerServerError() throws Exception {

        Owner owner = new Owner("Victoria", "MadeThisTest1", "email@email.com", "VerySecure123");
        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerRepository.createOwner(any(Owner.class)))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore failure")));

        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerRepository.createOwner(any(Owner.class)))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetOwnerByIdOk() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "123@gmail.com", "VerySecure123");

        when(ownerRepository.getOwnerById(mockDocId)).thenReturn(new OwnerDTO(mockDocId, owner));

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Victoria"))
                .andExpect(jsonPath("$.lastName").value("MadeThisTest1"))
                .andExpect(jsonPath("$.email").value("123@gmail.com"))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testGetOwnerByIdNotFound() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "   ", "VerySecure123");

        // null id
        when(ownerRepository.getOwnerById(mockDocId)).thenReturn(new OwnerDTO(null, owner));

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // null dto
        when(ownerRepository.getOwnerById(mockDocId)).thenReturn(null);

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // empty id
        when(ownerRepository.getOwnerById(any(String.class)))
                .thenReturn(new OwnerDTO("", owner));
        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOwnerByIdServerError() throws Exception {
        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerRepository.getOwnerById(mockDocId))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerRepository.getOwnerById(any(String.class)))
                .thenAnswer(_ -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateOwnerOk() throws Exception {
        Owner owner = new Owner("NewName", "", "", "VerySecure123");
        when(ownerRepository.updateOwner(any(String.class), any(Map.class)))
                .thenReturn(new OwnerDTO(mockDocId, owner));

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.lastName").value(""))
                .andExpect(jsonPath("$.email").value(""))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testUpdateOwnerBadRequest() throws Exception {
        Owner owner = new Owner("", "", null, null);
        when(ownerRepository.updateOwner(any(String.class), any(Map.class)))
                .thenReturn(new OwnerDTO(mockDocId, owner));

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateOwnerNotFound() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "123@abc.com", "123");
        when(ownerRepository.updateOwner(any(String.class), any(Map.class)))
                .thenReturn(new OwnerDTO(null, owner));

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());

        when(ownerRepository.updateOwner(any(String.class), any(Map.class)))
                .thenReturn(null);

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateOwnerServerError() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "email@email.com", "VerySecure123");

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerRepository.updateOwner(any(String.class), any(Map.class)))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerRepository.updateOwner(any(String.class), any(Map.class)))
                .thenAnswer(_ -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteOwnerNoContent() throws Exception {
        Owner owner = new Owner("NewName", "NewName", "new@name.com", "VerySecure123");
        when(ownerRepository.getOwnerById(any(String.class)))
                .thenReturn(new OwnerDTO(mockDocId, owner));
        when(ownerRepository.deleteOwner(any(String.class)))
                .thenReturn(true);

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteOwnerNotFound() throws Exception {
        // null ID
        Owner owner = new Owner("NewName", "NewName", "new@name.com", "VerySecure123");
        when(ownerRepository.getOwnerById(any(String.class)))
                .thenReturn(new OwnerDTO(null, owner));

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());

        // null DTO
        when(ownerRepository.getOwnerById(any(String.class)))
                .thenReturn(null);

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());

        // empty id
        when(ownerRepository.getOwnerById(any(String.class)))
                .thenReturn(new OwnerDTO("", owner));

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOwnerServerError() throws Exception {
        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerRepository.getOwnerById(mockDocId))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerRepository.getOwnerById(any(String.class)))
                .thenAnswer(_ -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId))
                .andExpect(status().isInternalServerError());
    }
}
