package com.softeng.backend.controllers.user.owner;

import com.softeng.backend.dto.OwnerDTO;
import com.softeng.backend.models.user.owner.Owner;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface IOwnerController {

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody Owner owner);

    /*****************************************************************************
     * READ
     ******************************************************************************/
    ResponseEntity<OwnerDTO> ownerLogin(@Valid @RequestParam String email, @Valid @RequestParam String password);
    ResponseEntity<OwnerDTO> getOwnerByEmail(@Valid @RequestParam String email);
    ResponseEntity<OwnerDTO> getOwnerById(@Valid @RequestParam String id);

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    ResponseEntity<OwnerDTO> updateOwner(@PathVariable String id, @Valid @RequestBody Owner owner);
}
