package com.softeng.backend.services.user.vet;

import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.models.user.vet.Vet;
import com.softeng.backend.repository.user.vet.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

// // The following code was copied with guidance from OpenAI's ChatGPT (https://chat.openai.com)
// Reference: was asking ChatGPT for basic Service setup (for testing) when making this

@Slf4j
@Service
public class VetService implements IVetService {

    private final VetRepository vetRepository;

    public VetService(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    public VetDTO createVet(Vet vet) throws ExecutionException, InterruptedException {
        VetDTO dto;
        if (vet == null || vet.checkInvalidUser()) {
            log.debug("DEBUG LOG: Vet service detected invalid user for creation: {}", vet == null ? null : vet.getEmail());
            dto = new VetDTO();
        } else {
            VetDTO existingVet = getVetByEmail(vet.getEmail());
            if (existingVet != null && existingVet.getVet() != null && !existingVet.getVet().checkEmptyUser()) {
                log.debug("DEBUG LOG: Vet service detected user that already exists: {}", vet.getEmail());
                dto = new VetDTO();
            } else {
                dto = vetRepository.createVet(vet);
            }
        }
        return dto;
    }

    /*****************************************************************************
     * READ
     ******************************************************************************/
    public VetDTO getVetByEmail(String email) throws ExecutionException, InterruptedException {
        return vetRepository.getVetByEmail(email);
    }

    public VetDTO getVetById(String id) throws ExecutionException, InterruptedException {
        return vetRepository.getVetById(id);
    }

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    // https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
    public VetDTO updateVet(String id, Vet vet) throws ExecutionException, InterruptedException {

        if (vet.checkEmptyUser()) {
            return new VetDTO();
        }

        Map<String, Object> updateFields = new HashMap<>();
        if (vet.getFirstName() != null) {
            updateFields.put("firstName", vet.getFirstName());
        }
        if (vet.getLastName() != null) {
            updateFields.put("lastName", vet.getLastName());
        }
        if (vet.getEmail() != null) {
            updateFields.put("email", vet.getEmail());
        }
        if (vet.getCertification() != null) {
            updateFields.put("certification", vet.getCertification());
        }
        return vetRepository.updateVet(id, updateFields);
    }

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    public void deleteVet(String id) throws ExecutionException, InterruptedException {
        vetRepository.deleteVet(id);
    }
}
