package com.softeng.backend.services.user.vet;

import org.springframework.stereotype.Service;
import com.softeng.backend.repository.user.vet.VetRepository;
import com.softeng.backend.models.user.vet.Vet;

import java.util.List;

// // The following code was copied with guidance from OpenAI's ChatGPT (https://chat.openai.com)
// Reference: was asking ChatGPT for basic Service setup (for testing) when making this
// TODO: needs to be implemented

@Service
public class VetService {

    private final VetRepository vetRepository;

    public VetService(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    public List<Vet> getAllVets() {
        return vetRepository.getAllVets();
    }

    public List<Vet> getVetsByName(String name) {
        return vetRepository.getByName(name);
    }

}