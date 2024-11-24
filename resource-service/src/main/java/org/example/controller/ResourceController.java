package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.ResourceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;

@RestController
@RequestMapping("/resources")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceController {

    private final ResourceRestService resourceService;

    @PostMapping(consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<Identifiable<Integer>> uploadResource(@RequestBody byte[] bytes) {

        return ResponseEntity.ok()
                .body(resourceService.storeFile(bytes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getBinaryAudioData(@PathVariable("id") String id) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add(ACCEPT_ENCODING, "identity");

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(resourceService.getAudioData(id));
    }

    @DeleteMapping
    public List<Integer> deleteResources(@RequestParam(required = false, name = "ids") String ids) {
        return resourceService.deleteResources(ids);
    }

}
