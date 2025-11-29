package com.softeng.backend.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.controllers.user.owner.OwnerController;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.PetDTO;
import com.softeng.backend.exception.repository.DocumentNotFoundException;
import com.softeng.backend.models.enums.AnimalGroup;
import com.softeng.backend.models.enums.PetSexType;
import com.softeng.backend.models.enums.SterileStatus;
import com.softeng.backend.models.pet.Pet;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.services.pet.PetService;
import com.softeng.backend.services.user.owner.OwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


/**
 * UserControllerTest
 * Test cases written by Victoria Iskandar.
 * Reference: OpenAI ChatGPT GPT-5Mini (<a href="https://chat.openai.com">...</a>)
 * Some JSON formatting and MockMvc syntax guidance was copied from ChatGPT, which
 * are referenced in line.
 * For all test cases I copied some code from chatGPT relating to any() syntax and
 * when mocking the service, and the response validation jsonPath() calls, also I learned from ChatGPT about
 * ObjectMapper.
 */

@WebMvcTest(controllers = OwnerController.class)
public class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;
    @MockitoBean
    private PetService petService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String mockDocId = "mockDocId";

    //TODO: remove when we set up auth
    private static final String mockToken = "MockTokenForNow";

    @Test
    void testCreateOwnerCreated() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "123@gmail.com", "VerySecure123");
        when(ownerService.createOwner(any(Owner.class))).thenReturn(new OwnerDTO(mockDocId, owner));

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
        when(ownerService.createOwner(any(Owner.class))).thenReturn(new OwnerDTO(null, owner));
        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Conflict fields"))
                .andExpect(jsonPath("$.detail.email").value("Email already exists"));

        // null DTO path:
        when(ownerService.createOwner(any(Owner.class))).thenReturn(null);
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
        when(ownerService.createOwner(any(Owner.class))).thenReturn(null);
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
        when(ownerService.createOwner(any(Owner.class)))
                .thenThrow(new ExecutionException(new RuntimeException("Firestore failure")));

        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerService.createOwner(any(Owner.class)))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(post("/api/v1/owners/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetOwnerByIdOk() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "123@gmail.com", "VerySecure123");

        when(ownerService.getOwnerById(mockDocId)).thenReturn(new OwnerDTO(mockDocId, owner));

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
        when(ownerService.getOwnerById(mockDocId)).thenReturn(new OwnerDTO(null, owner));

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());

        // null dto
        when(ownerService.getOwnerById(mockDocId)).thenReturn(null);

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // empty id
        when(ownerService.getOwnerById(any(String.class)))
                .thenReturn(new OwnerDTO("", owner));
        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOwnerByIdServerError() throws Exception {
        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerService.getOwnerById(mockDocId))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId))
                        .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerService.getOwnerById(any(String.class)))
                .thenAnswer(_ -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateOwnerOk() throws Exception {
        Owner owner = new Owner("NewName", "", "", "VerySecure123");
        when(ownerService.updateOwner(any(String.class), any(Owner.class)))
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
        when(ownerService.updateOwner(any(String.class), any(Owner.class)))
                .thenReturn(new OwnerDTO(mockDocId, owner));

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateOwnerNotFound() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "123@abc.com", "123");
        when(ownerService.updateOwner(any(String.class), any(Owner.class)))
                .thenReturn(new OwnerDTO(null, owner));

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());

        when(ownerService.updateOwner(any(String.class), any(Owner.class)))
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
        when(ownerService.updateOwner(any(String.class), any(Owner.class)))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerService.updateOwner(any(String.class), any(Owner.class)))
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
        when(ownerService.getOwnerById(any(String.class)))
                .thenReturn(new OwnerDTO(mockDocId, owner));
        when(ownerService.deleteOwner(any(String.class)))
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
        when(ownerService.getOwnerById(any(String.class)))
                .thenReturn(new OwnerDTO(null, owner));

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());

        // null DTO
        when(ownerService.getOwnerById(any(String.class)))
                .thenReturn(null);

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());

        // empty id
        when(ownerService.getOwnerById(any(String.class)))
                .thenReturn(new OwnerDTO("", owner));

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOwnerServerError() throws Exception {
        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerService.getOwnerById(mockDocId))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId))
                .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerService.getOwnerById(any(String.class)))
                .thenAnswer(_ -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(delete("/api/v1/owners/{id}", mockDocId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetOwnersPetsSuccess() throws Exception {
        // Mock data
        Date date = new Date();
        Pet pet1 = new Pet("Bin", "owner123", "dog", "husky", true, PetSexType.FEMALE, date, SterileStatus.STERILE, AnimalGroup.SMALL_MAMMAL);
        Pet pet2 = new Pet("Neko", "owner123", "cat", "persian", false, PetSexType.MALE, date, SterileStatus.UNKNOWN, AnimalGroup.SMALL_MAMMAL);
        List<PetDTO> pets = List.of(new PetDTO("1", pet1), new PetDTO("2", pet2));

        when(petService.getPetsByOwnerId(anyString())).thenReturn(pets);

        mockMvc.perform(get("/api/v1/owners/{ownerId}/pets", mockDocId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // First pet
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Bin"))
                .andExpect(jsonPath("$[0].ownerId").value("owner123"))
                .andExpect(jsonPath("$[0].species").value("dog"))
                .andExpect(jsonPath("$[0].breed").value("husky"))
                .andExpect(jsonPath("$[0].sex").value("FEMALE"))
                .andExpect(jsonPath("$[0].birthdate").value(date.toString()))
                .andExpect(jsonPath("$[0].sterileStatus").value("STERILE"))
                .andExpect(jsonPath("$[0].animalGroup").value("SMALL_MAMMAL"))
                .andExpect(jsonPath("$[0].estimatedBirthdate").value(true))

                // Second pet
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Neko"))
                .andExpect(jsonPath("$[1].ownerId").value("owner123"))
                .andExpect(jsonPath("$[1].species").value("cat"))
                .andExpect(jsonPath("$[1].breed").value("persian"))
                .andExpect(jsonPath("$[1].sex").value("MALE"))
                .andExpect(jsonPath("$[1].birthdate").value(date.toString()))
                .andExpect(jsonPath("$[1].sterileStatus").value("UNKNOWN"))
                .andExpect(jsonPath("$[1].animalGroup").value("SMALL_MAMMAL"))
                .andExpect(jsonPath("$[1].estimatedBirthdate").value(false));
    }

    @Test
    void testGetOwnersPetsEmptyList() throws Exception {
        when(petService.getPetsByOwnerId(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/owners/{ownerId}/pets", mockDocId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); // ensure empty array
    }

    @Test
    void testGetOwnersPetsInternalServerError() throws Exception {
        when(petService.getPetsByOwnerId(anyString())).thenThrow(ExecutionException.class);

        mockMvc.perform(get("/api/v1/owners/{ownerId}/pets", mockDocId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetOwnersPetsNotFound() throws Exception {
        when(petService.getPetsByOwnerId(anyString())).thenThrow(DocumentNotFoundException.class);

        mockMvc.perform(get("/api/v1/owners/{ownerId}/pets", mockDocId))
                .andExpect(status().isNotFound());
    }
}
