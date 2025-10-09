package com.softeng.backend.services;

import org.springframework.stereotype.Service;
import com.softeng.backend.repository.VetRepository;
import com.softeng.backend.models.Vet;

import java.util.List;

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
        return vetRepository.findByName(name);
    }

}