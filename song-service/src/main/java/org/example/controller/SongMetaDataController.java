package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.service.rest.SongMetaDataRestService;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.SongMetaDataDto;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SongMetaDataController {

    private final SongMetaDataRestService songMetaDataRestService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Identifiable<Integer>> createMetadata(@RequestBody SongMetaDataDto songMetaDataDto) {
        Session
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(songMetaDataRestService.storeMetaData(songMetaDataDto));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SongMetaDataDto> getSongMetaData(@PathVariable String id) {
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(songMetaDataRestService.getMetaData(id));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Integer>> deleteResources(@RequestParam(required = false, name = "ids") String ids) {
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(songMetaDataRestService.deleteMetaData(ids));
    }
}
