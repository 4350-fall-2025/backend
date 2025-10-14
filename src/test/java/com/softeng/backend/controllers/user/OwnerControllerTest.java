package com.softeng.backend.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.controllers.user.owner.OwnerController;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.services.user.owner.OwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


/**
 * UserControllerTest
 * Test cases written by Victoria Iskandar.
 * Some JSON formatting and MockMvc syntax guidance was copied from OpenAI ChatGPT GPT-5Mini, which
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

    @Autowired
    private ObjectMapper objectMapper;

    private static final String mockDocId = "mockDocId";

    //TODO: remove when we set up auth
    private static String mockPass = "MockTokenForNow";

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
                .andExpect(jsonPath("$.token").value(mockPass));
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
                .andExpect(jsonPath("$.token").value(mockPass));
    }

    @Test
    void testGetOwnerByIdNotFound() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "   ", "VerySecure123");

        when(ownerService.getOwnerById(mockDocId)).thenReturn(new OwnerDTO(null, owner));

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
    }

    @Test
    void testGetOwnerByIdServerError() throws Exception {
        Owner owner = new Owner("Victoria", "MadeThisTest1", "email@email.com", "VerySecure123");

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerService.getOwnerById(mockDocId))
                .thenThrow(new InterruptedException("Operation was interrupted"));

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId))
                        .andExpect(status().isInternalServerError());

        // this when/thenThrow statement was copied from ChatGPT:
        when(ownerService.getOwnerById(any(String.class)))
                .thenAnswer(invocation -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(get("/api/v1/owners/{id}", mockDocId))
                .andExpect(status().isInternalServerError());
    }

    /*
    * @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOwner(@PathVariable String id, @Valid @RequestBody Owner owner) {

        OwnerDTO dto;
        if (owner == null || owner.checkEmptyUser()) {
            logger.debug("DEBUG LOG: Owner update /id endpoint detected empty request: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            dto = ownerService.updateOwner(id, owner);
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("DEBUG LOG: Owner /update endpoint failed: {}", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().build();
        }

        if (dto == null || dto.getId() == null) {
            logger.debug("DEBUG LOG: Owner update /id endpoint not found for id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }*/
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
                .andExpect(jsonPath("$.token").value(mockPass));
    }

//    @Test
//    void testUpdateOwnerBadRequest() throws Exception {
//
//    }
//
//    @Test
//    void testUpdateOwnerNotFound() throws Exception {
//
//    }

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
                .thenAnswer(invocation -> {
                    throw new ExecutionException(new RuntimeException("Firestore failure"));
                });

        this.mockMvc.perform(put("/api/v1/owners/{id}", mockDocId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(owner)))
                .andExpect(status().isInternalServerError());
    }

//    @Test
//    void testDeleteOwnerOk() throws Exception {
//
//    }
//
//    @Test
//    void testDeleteOwnerNoContent() throws Exception {
//
//    }
//
//    @Test
//    void testDeleteOwnerNotFound() throws Exception {
//
//    }
//
//    @Test
//    void testDeleteOwnerServerError() throws Exception {
//
//    }
}
