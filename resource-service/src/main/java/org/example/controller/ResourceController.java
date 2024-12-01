package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.service.validator.annotation.IdValidation;
import org.example.service.validator.annotation.IdsValidation;
import org.example.service.dto.Identifiable;
import org.example.service.rest.ResourceRestService;
import org.example.service.dto.Identifiables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;

@RestController
@RequestMapping("/resources")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class ResourceController {

    private final ResourceRestService resourceService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<Identifiable<Integer>> uploadResource(@RequestBody byte[] bytes) {

        return ResponseEntity.ok()
                .body(resourceService.storeFile(bytes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getBinaryAudioData(@PathVariable("id") @IdValidation String id) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("audio","mpeg"));
        httpHeaders.add(ACCEPT_ENCODING, "identity");

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(resourceService.getAudioData(id));
    }

    @DeleteMapping
    public Identifiables<Integer> deleteResources(@RequestParam(required = false, name = "id") @IdsValidation String ids) {
        return resourceService.deleteResources(ids);
    }

}
