package com.softeng.backend.controllers.user.owner;

import com.softeng.backend.models.user.owner.Owner;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface IOwnerController {

    /*****************************************************************************
     * CREATE
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> createOwner(@Valid @RequestBody Owner owner);

    /*****************************************************************************
     * READ
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> getOwnerById(@PathVariable String id);
    ResponseEntity<List<Map<String, Object>>> getOwnersPets(@PathVariable String ownerId);
    /*****************************************************************************
     * UPDATE
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> updateOwner(@PathVariable String id, @Valid @RequestBody Owner owner);

    /*****************************************************************************
     * DELETE
     ******************************************************************************/
    ResponseEntity<Map<String, Object>> deleteOwner(@PathVariable String id);
}
