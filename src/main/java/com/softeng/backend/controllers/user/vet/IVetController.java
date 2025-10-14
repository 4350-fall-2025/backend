package com.softeng.backend.controllers.user.vet;

import com.softeng.backend.models.user.vet.Vet;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface IVetController {
    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> createVet(@Valid @RequestBody Vet vet);

    /*****************************************************************************
     * READ
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> getVetById(@PathVariable String id);

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> updateVet(@PathVariable String id, @Valid @RequestBody Vet vet);

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> deleteVet(@PathVariable String id);
}
