package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.service.rest.SongMetaDataRestService;
import org.example.service.rest.dto.Identifiable;
import org.example.service.rest.dto.SongMetaDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/songs")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SongMetaDataController {

    private final SongMetaDataRestService songMetaDataRestService;

    @PostMapping
    public Identifiable<Integer> createMetadata(@RequestBody SongMetaDataDto songMetaDataDto) {
        return songMetaDataRestService.storeMetaData(songMetaDataDto);
    }

    @GetMapping("/{id}")
    public SongMetaDataDto getSongMetaData(@PathVariable String id) {
        return songMetaDataRestService.getMetaData(id);
    }

    @DeleteMapping
    public List<Integer> deleteResources(@RequestParam(required = false, name = "ids") String ids) {
        return songMetaDataRestService.deleteMetaData(ids);
    }
}
