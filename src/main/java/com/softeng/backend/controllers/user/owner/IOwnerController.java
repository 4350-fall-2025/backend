package com.softeng.backend.controllers.user.owner;

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
    public ResponseEntity<String> createOwner(@Valid @RequestBody Owner owner);

    /*****************************************************************************
     * READ
     ******************************************************************************/
    public ResponseEntity<String> ownerLogin(@Valid @RequestParam String email, @Valid @RequestParam String password);
    public ResponseEntity<Owner> getOwnerByEmail(@Valid @RequestParam String email);
    public ResponseEntity<Owner> getOwnerById(@Valid @RequestParam String id);

    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    public ResponseEntity<Owner> updateOwner(@PathVariable String id, @Valid @RequestBody Owner owner);
}
