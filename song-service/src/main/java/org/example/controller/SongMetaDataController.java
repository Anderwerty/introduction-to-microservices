package org.example.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.service.rest.SongMetaDataRestService;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.Identifiables;
import org.example.service.rest.dto.SongMetaDataDto;
import org.example.service.validator.annotation.IdValidation;
import org.example.service.validator.annotation.IdsValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songs")
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class SongMetaDataController {

    private final SongMetaDataRestService songMetaDataRestService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Identifiable<Integer>> createMetadata(@RequestBody @Valid SongMetaDataDto songMetaDataDto) {
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(songMetaDataRestService.storeMetaData(songMetaDataDto));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SongMetaDataDto> getSongMetaData(@PathVariable
                                                           @IdValidation String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(songMetaDataRestService.getMetaData(id));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Identifiables<Integer>> deleteResources(@RequestParam(required = false, name = "id")
                                                                  @IdsValidation String ids) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(songMetaDataRestService.deleteMetaData(ids));
    }
}
