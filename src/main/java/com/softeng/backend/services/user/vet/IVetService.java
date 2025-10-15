package com.softeng.backend.services.user.vet;

import com.softeng.backend.dto.VetDTO;
import com.softeng.backend.models.user.vet.Vet;

import java.util.concurrent.ExecutionException;

public interface IVetService {

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    VetDTO createVet(Vet vet) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * READ
     ******************************************************************************/
    VetDTO getVetByEmail(String email)  throws ExecutionException, InterruptedException;
    VetDTO getVetById(String id) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    VetDTO updateVet(String id, Vet vet) throws ExecutionException, InterruptedException;

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    boolean deleteVet(String id);
}
