package com.softeng.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.repository.pet.PetRepository;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import com.softeng.backend.services.pet.PetService;
import com.softeng.backend.services.user.owner.OwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Sprint 2:
 * Assistance provided by OpenAI’s GPT-5 Mini language model (ChatGPT), October 2025.
 * Asked for help rewriting ALL these tests to stop using the mock repo since
 * it will use the emulator.
 * Thus, ALL test functions in this file were originally written by Minh Phan,
 * then updated & copied from ChatGPT
 * and reviewed by Victoria Iskandar to ensure correctness.
 */

@SpringBootTest
@AutoConfigureMockMvc
public class OwnerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
  
    @Autowired
    private ObjectMapper objectMapper;

    private static final String mockToken = "MockTokenForNow";

    private static final Owner ownerToGet = new Owner("Default", "Owner", "defaultowner@gmail.com", "StrongPass123!");

    private static final Owner ownerToUpdate = new Owner("Always", "Changing", "ichange@gmail.com", "StrongPass123!");

    private static String ownerToUpdateId = "";

    private static String ownerToGetId = "";

    @Autowired
    private OwnerRepository ownerRepository;
  
    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setupOwner() throws Exception {
        ownerToGetId = ownerRepository.createOwner(ownerToGet).getId();
        ownerToUpdateId = ownerRepository.createOwner(ownerToUpdate).getId();
    }

    @Test
    void testCreateOwnerCreated() throws Exception {
        Owner createdOwner = new Owner("Arya", "Lancelot", "email@gmail.com", "StrongPass123!");
        mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdOwner)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(createdOwner.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createdOwner.getLastName()))
                .andExpect(jsonPath("$.email").value(createdOwner.getEmail()))
                .andExpect(jsonPath("$.token").value(mockToken));

        String ownerToCreateId = ownerRepository.getOwnerByEmail(createdOwner.getEmail()).getId();
        ownerRepository.deleteOwner(ownerToCreateId);
    }

    @Test
    void testCreateOwnerConflict() throws Exception {;
        mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerToGet)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict fields"))
                .andExpect(jsonPath("$.detail.email").value("Email already exists"));
    }

    @Test
    void testCreateOwnerBadRequest() throws Exception {
        Owner invalidOwner = new Owner("Arya", "Lancelot", "   ", "short");

        mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOwner)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.detail.firstName").value("Name cannot be empty"))
                .andExpect(jsonPath("$.detail.lastName").value("Name cannot be empty"))
                .andExpect(jsonPath("$.detail.email").value("Invalid email format"))
                .andExpect(jsonPath("$.detail.password").value("Must be at least 8 characters"));
    }

    @Test
    void testGetOwnerByIdOk() throws Exception {
        mockMvc.perform(get("/api/v1/owners/{id}", ownerToGetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(ownerToGet.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(ownerToGet.getLastName()))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testUpdateOwnerOk() throws Exception {
        Owner updateOwner = new Owner("NewName", "", "", "StrongPass123!");
        mockMvc.perform(put("/api/v1/owners/{id}", ownerToUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOwner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.lastName").value(""))
                .andExpect(jsonPath("$.email").value(""))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testUpdateOwnerBadRequest() throws Exception {
        Owner invalidUpdate = new Owner("", "", null, null);
        mockMvc.perform(put("/api/v1/owners/{id}", ownerToUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateOwnerNotFound() throws Exception {
        Owner updateOwner = new Owner("NoOne", "None", "noone@gmail.com", "password123!");
        mockMvc.perform(put("/api/v1/owners/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOwner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOwnerNoContent() throws Exception {
        // Create a temporary owner to delete
        String email = "temp.owner+" + System.currentTimeMillis() + "@gmail.com";
        Owner tempOwner = new Owner("Temp", "Owner", email, "StrongPass123!");

        String responseJson = mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tempOwner)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> createdOwner = objectMapper.readValue(responseJson, Map.class);
        String tempOwnerId = (String) createdOwner.get("id");

        mockMvc.perform(delete("/api/v1/owners/{id}", tempOwnerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/owners/{id}", tempOwnerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOwnerNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/owners/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @AfterEach
    void tearDown() throws Exception {
        ownerRepository.deleteOwner(ownerToGetId);
        ownerRepository.deleteOwner(ownerToUpdateId);
    }
}
