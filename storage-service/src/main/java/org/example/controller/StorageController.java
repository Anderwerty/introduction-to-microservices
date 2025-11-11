package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.service.dto.StorageCreationRequest;
import org.example.service.dto.StorageDetailsResponse;
import org.example.service.validator.annotation.IdsValidation;
import org.example.service.dto.Identifiable;
import org.example.service.rest.StorageRestService;
import org.example.service.dto.Identifiables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storages")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class StorageController {

    private final StorageRestService storageRestService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<Identifiable<Integer>> createStorage(@RequestBody StorageCreationRequest request) {

        return ResponseEntity.ok()
                .body(storageRestService.createStorage(request));
    }

    @GetMapping
    public ResponseEntity<List<StorageDetailsResponse>> getAllStorage() {

        return ResponseEntity.ok()
                .body(storageRestService.getAllStorages());
    }

    @DeleteMapping
    public ResponseEntity<Identifiables<Integer>> deleteStorages(@RequestParam(required = false, name = "id")
                                                                     @IdsValidation String ids) {
        return ResponseEntity.ok().body(storageRestService.deleteStorages(ids));
    }

}
