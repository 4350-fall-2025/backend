package com.softeng.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.repository.user.vet.VetRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@ActiveProfiles("emulator")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VetIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String mockCert = "Certified";

    private static final Vet vetToGet = new Vet("Default", "Vet", "defaultvet@gmail.com", "StrongPass123!", mockCert);

    private static final Vet vetToUpdate = new Vet("Always", "Changing", "ichange@gmail.com", "StrongPass123!", mockCert);

    private static String vetToUpdateId = "";

    private static String vetToGetId = "";

    @Autowired
    private VetRepository vetRepository;

    //TODO: remove when we set up auth
    private static final String mockPass = "MockTokenForNow";

    @BeforeAll
    void setupVets() throws Exception {
        vetToGetId = vetRepository.createVet(vetToGet).getId();
        vetToUpdateId = vetRepository.createVet(vetToUpdate).getId();
    }

    @Test
    void testCreateVetCreated() throws Exception {
        Vet createdVet = new Vet("Arya", "Lancelot", "vet2@gmail.com", "StrongPass123!", mockCert);
        mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdVet)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(createdVet.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createdVet.getLastName()))
                .andExpect(jsonPath("$.email").value(createdVet.getEmail()))
                .andExpect(jsonPath("$.token").value(mockPass));
        String ownerToCreateId = vetRepository.getVetByEmail(createdVet.getEmail()).getId();
        vetRepository.deleteVet(ownerToCreateId);
    }

    @Test
    void testCreateVetConflict() throws Exception {
        mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vetToGet)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict fields"))
                .andExpect(jsonPath("$.detail.email").value("Email already exists"));
    }

    @Test
    void testCreateVetBadRequest() throws Exception {
        Vet invalidVet = new Vet("Arya", "Lancelot", "   ", "short", mockCert);

        mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidVet)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.detail.firstName").value("Name cannot be empty"))
                .andExpect(jsonPath("$.detail.lastName").value("Name cannot be empty"))
                .andExpect(jsonPath("$.detail.email").value("Invalid email format"))
                .andExpect(jsonPath("$.detail.password").value("Must be at least 8 characters"));
    }

    @Test
    void testGetVetByIdOk() throws Exception {
        mockMvc.perform(get("/api/v1/vets/{id}", vetToGetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(vetToGet.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(vetToGet.getLastName()))
                .andExpect(jsonPath("$.token").value(mockPass));
    }

    @Test
    void testUpdateVetOk() throws Exception {
        Vet updateVet = new Vet("NewName", "", "", "StrongPass123!", mockCert);
        mockMvc.perform(put("/api/v1/vets/{id}", vetToUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateVet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.lastName").value(""))
                .andExpect(jsonPath("$.email").value(""))
                .andExpect(jsonPath("$.token").value(mockPass));
    }

    @Test
    void testUpdateVetBadRequest() throws Exception {
        Vet invalidUpdate = new Vet("", "", null, null, null);
        mockMvc.perform(put("/api/v1/vets/{id}", vetToUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateVetNotFound() throws Exception {
        Vet updateVet = new Vet("NoOne", "None", "noone@gmail.com", "password123!", mockCert);
        mockMvc.perform(put("/api/v1/vets/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateVet)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteVetNoContent() throws Exception {
        // Create a temporary vet to delete
        String email = "temp.vet+" + System.currentTimeMillis() + "@gmail.com";
        Vet tempVet = new Vet("Temp", "Vet", email, "StrongPass123!", mockCert);

        String responseJson = mockMvc.perform(post("/api/v1/vets/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tempVet)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> createdVet = objectMapper.readValue(responseJson, Map.class);
        String tempVetId = (String) createdVet.get("id");

        mockMvc.perform(delete("/api/v1/vets/{id}", tempVetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/vets/{id}", tempVetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteVetNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/vets/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @AfterAll
    void tearDown() throws Exception {
        vetRepository.deleteVet(vetToGetId);
        vetRepository.deleteVet(vetToUpdateId);
    }
}
