package org.example.service.rest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Identifiable<ID> {
    private ID id;

}
