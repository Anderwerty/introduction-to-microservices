package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.dto.*;
import org.example.service.rest.StorageRestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StorageController.class)
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageRestService storageRestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createStorageShouldReturnIdentifiable() throws Exception {
        StorageCreationRequest request = new StorageCreationRequest();
        request.setBucket("bucket1");
        request.setPath("/some/path");
        request.setStorageType(StorageType.STAGING);

        Identifiable<Integer> response = new Identifiable<>(123);
        Mockito.when(storageRestService.createStorage(any(StorageCreationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/storages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123));

        Mockito.verify(storageRestService).createStorage(any(StorageCreationRequest.class));
    }

    @Test
    void getAllStorageShouldReturnList() throws Exception {
        List<StorageDetailsResponse> storages = List.of(
                new StorageDetailsResponse(1, StorageType.STAGING, "bucket1", "/path1"),
                new StorageDetailsResponse(2, StorageType.PERMANENT, "bucket2", "/path2")
        );

        Mockito.when(storageRestService.getAllStorages()).thenReturn(storages);

        mockMvc.perform(get("/storages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bucket").value("bucket1"))
                .andExpect(jsonPath("$[1].storageType").value("PERMANENT"));

        Mockito.verify(storageRestService).getAllStorages();
    }

    @Test
    void deleteStoragesShouldReturnDeletedIds() throws Exception {
        Identifiables<Integer> ids = new Identifiables<>(List.of(1, 2, 3));
        Mockito.when(storageRestService.deleteStorages(eq("1,2,3"))).thenReturn(ids);

        mockMvc.perform(delete("/storages")
                        .param("id", "1,2,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(3)))
                .andExpect(jsonPath("$.ids[0]").value(1))
                .andExpect(jsonPath("$.ids[2]").value(3));

        Mockito.verify(storageRestService).deleteStorages("1,2,3");
    }

    @Test
    void deleteStoragesShouldReturnOkWhenNoIdParam() throws Exception {
        Identifiables<Integer> empty = new Identifiables<>(List.of());
        Mockito.when(storageRestService.deleteStorages(null)).thenReturn(empty);

        mockMvc.perform(delete("/storages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", hasSize(0)));

        Mockito.verify(storageRestService).deleteStorages(null);
    }
}
