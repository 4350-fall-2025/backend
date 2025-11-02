package com.softeng.backend.integration;

import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.repository.user.owner.OwnerRepository;
import com.softeng.backend.repository.user.vet.VetRepository;
import com.softeng.backend.services.user.AuthService;
import com.softeng.backend.services.user.owner.OwnerService;
import com.softeng.backend.services.user.vet.VetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * AuthIntegrationTest
 * Test cases written by Minh Phan.
 * Reference: OpenAI ChatGPT GPT-5Mini (<a href="https://chat.openai.com">...</a>)
 * Some JSON formatting and MockMvc syntax guidance was copied from ChatGPT, which
 * are referenced in line.
 * For all test cases I copied some code from chatGPT relating to any() syntax and
 * when mocking the service, and the response validation jsonPath() calls
 * Sprint 2: Asked ChatGPT for assistance when issues occurred when switching from mock repo
 * to hitting the emulator, code written with assistance from ChatGPT.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("emulator")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private VetRepository vetRepository;
    @MockitoBean
    private AuthService authService;

    private static final String mockToken = "MockTokenForNow";

    private static final Owner owner = new Owner("Arya", "Lancelot", "owner@gmail.com", "passwordHasSpecialCh4r@cters");
    private static  final Vet vet = new Vet("Arya", "Lancelot", "vet@gmail.com", "passwordHasSpecialCh4r@cters", "Certified");
    private static String ownerId = "";
    private static String vetId = "";

    // BeforeAll section was created with assistance from ChatGPT
    @BeforeEach
    void setup() throws Exception {
        ownerId = ownerRepository.createOwner(owner).getId();
        vetId = vetRepository.createVet(vet).getId();
        when(authService.createCustomToken((any(String.class)), (any(String.class)))).thenReturn(mockToken);
    }

    @Test
    void testLoginOwnerSuccess() throws Exception {
        // Test Owner Login
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content("{\"email\":\"owner@gmail.com\",\"password\":\"passwordHasSpecialCh4r@cters\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(owner.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(owner.getLastName()))
                .andExpect(jsonPath("$.email").value(owner.getEmail()))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testLoginVetSuccess() throws Exception {

        // Test Vet Login
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content("{\"email\":\"vet@gmail.com\",\"password\":\"passwordHasSpecialCh4r@cters\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(vet.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(vet.getLastName()))
                .andExpect(jsonPath("$.email").value(vet.getEmail()))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testLoginEmailNotFound() throws Exception {
        // Test Email Not Found
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content("{\"email\":\"notemail@gmail.com\",\"password\":\"passwordHasSpecialCh4r@cters\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Incorrect Credential"))
                .andExpect(jsonPath("$.detail").value("Email does not exist"));
    }

    @Test
    void testLoginPasswordIncorrect() throws Exception {
        // Test Password Incorrect
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content("{\"email\":\"owner@gmail.com\",\"password\":\"WrongPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Incorrect Credential"))
                .andExpect(jsonPath("$.detail").value("Password is incorrect"));
    }

    @AfterEach
    void tearDown() {
        ownerRepository.deleteOwner(ownerId);
        vetRepository.deleteVet(vetId);
    }
}
