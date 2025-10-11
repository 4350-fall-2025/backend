package com.softeng.backend.controllers.user.vet;

import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.services.user.vet.VetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Reference: was asking ChatGPT for basic Controller setup (for testing) when making this
// TODO: needs to be implemented

@RestController
@RequestMapping("/api/v1/vets/id")
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

    // this is just to see how firestore emulator works
    @GetMapping("/search")
    public ResponseEntity<List<Vet>> getVetsByName(@RequestParam String name) {
        return ResponseEntity.ok(vetService.getVetsByName(name));
    }
}
