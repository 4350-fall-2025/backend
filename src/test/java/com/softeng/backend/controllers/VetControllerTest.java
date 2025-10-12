package com.softeng.backend.controllers;

import com.softeng.backend.models.Vet;
import com.softeng.backend.services.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = VetController.class)
class VetControllerTest {
    @Autowired
    private MockMvc  mockMvc;

    @MockitoBean
    private VetService  vetService;

    //since we dont have auth
    //TODO: once auth is setup - create a test that can run as a specific user
    //as detailed here: https://docs.spring.io/spring-boot/how-to/testing.html
    @Test
    void getAllVets() throws Exception {
        Vet vet1 = new Vet("01", "Basma", "AbdulRazaq");
        Vet vet2 = new Vet("02", "Sally", "Hansen");
        Vet vet3 = new Vet("03", "Crowd", "Mark");
        List<Vet> vets = new ArrayList<>(List.of(vet1, vet2, vet3));

        when(vetService.getAllVets()).thenReturn(vets);
        this.mockMvc.perform(get("/api/vets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getVetsByName() {
    }
}