package com.softeng.backend.repository.user.vet;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.models.user.vet.Vet;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IVetRepository {

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    VetDTO createVet(Vet vet) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * READ
     ******************************************************************************/
    VetDTO getVetByEmail(String email) throws ExecutionException, InterruptedException;
    VetDTO getVetById(String id) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    VetDTO updateVet(String id, Map<String, Object> updateFields) throws ExecutionException, InterruptedException;
}
