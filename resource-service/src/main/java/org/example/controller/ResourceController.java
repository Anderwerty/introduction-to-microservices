package org.example.controller;


import lombok.AllArgsConstructor;
import org.example.controller.dto.Identifiable;
import org.example.service.rest.ResourceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/resources")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceController {

    private final ResourceRestService resourceService;

    @PostMapping
    public Identifiable<Integer> uploadResource(@RequestPart("file") MultipartFile file) {
        return resourceService.storeFile(file);
    }

    @GetMapping("/{id}")
    public byte[] getBinaryAudioData(@PathVariable("id") String id) {
        return resourceService.getAudioData(id);
    }

    @DeleteMapping
    public List<Integer> deleteResources(@RequestParam(required = false, name = "ids") String ids) {
        return resourceService.deleteResources(ids);
    }

}
