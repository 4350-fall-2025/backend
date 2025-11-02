package com.softeng.backend.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.services.user.AuthService;
import com.softeng.backend.services.user.owner.OwnerService;
import com.softeng.backend.services.user.vet.VetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;
    @MockitoBean
    private VetService vetService;
    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String mockDocId = "mockDocId";
    private static final String mockToken = "MockTokenForNow";

    @BeforeEach
    void setUp() {
        when(authService.createCustomToken(eq(mockDocId), (any(String.class)))).thenReturn(mockToken);
    }

    @Test
    void testLoginOwnerSuccess() throws Exception {
        Owner owner = new Owner("Minh", "MadeThisTest1", "owner@gmail.com", "VerySecure123");

        when(ownerService.getOwnerByEmail(any(String.class))).thenReturn(new OwnerDTO(mockDocId, owner));
        when(vetService.getVetByEmail(any(String.class))).thenReturn(new VetDTO());
        // Test Owner Login
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"owner@gmail.com\",\"password\":\"VerySecure123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Minh"))
                .andExpect(jsonPath("$.lastName").value("MadeThisTest1"))
                .andExpect(jsonPath("$.email").value("owner@gmail.com"))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testLoginVetSuccess() throws Exception {
        Vet vet = new Vet("Minh", "MadeThisTest1", "vet@gmail.com", "VerySecure123", "Certified");

        when(ownerService.getOwnerByEmail(any(String.class))).thenReturn(new OwnerDTO());
        when(vetService.getVetByEmail(any(String.class))).thenReturn(new VetDTO(mockDocId, vet));
        // Test Vet Login
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"vet@gmail.com\",\"password\":\"VerySecure123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("Minh"))
                .andExpect(jsonPath("$.lastName").value("MadeThisTest1"))
                .andExpect(jsonPath("$.email").value("vet@gmail.com"))
                .andExpect(jsonPath("$.certification").value("Certified"))
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    void testLoginEmailNotFound() throws Exception {
        when(ownerService.getOwnerByEmail(any(String.class))).thenReturn(new OwnerDTO());
        when(vetService.getVetByEmail(any(String.class))).thenReturn(new VetDTO());
        // Test Email Not Found
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@gmail.com\",\"password\":\"VerySecure123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Incorrect Credential"))
                .andExpect(jsonPath("$.detail").value("Email does not exist"));
    }

    @Test
    void testLoginPasswordIncorrect() throws Exception {
        Owner owner = new Owner("Minh", "MadeThisTest1", "owner@gmail.com", "VerySecure123");

        when(ownerService.getOwnerByEmail(any(String.class))).thenReturn(new OwnerDTO(mockDocId, owner));
        when(vetService.getVetByEmail(any(String.class))).thenReturn(new VetDTO());
        // Test Password Incorrect
        this.mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"owner@gmail.com\",\"password\":\"WrongPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Incorrect Credential"))
                .andExpect(jsonPath("$.detail").value("Password is incorrect"));
    }
}
