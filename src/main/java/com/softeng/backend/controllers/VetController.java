package com.softeng.backend.controllers;

import com.softeng.backend.models.Vet;
import com.softeng.backend.services.VetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    @Autowired
    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<Vet>> getAllVets() {
        return ResponseEntity.ok(vetService.getAllVets());
    }

    // TODO: we will need to update this endpoint and add more, 
    // this is just to see how firestore emulator works
    @GetMapping("/search")
    public ResponseEntity<List<Vet>> getVetsByName(@RequestParam String name) {
        return ResponseEntity.ok(vetService.getVetsByName(name));
    }
}
