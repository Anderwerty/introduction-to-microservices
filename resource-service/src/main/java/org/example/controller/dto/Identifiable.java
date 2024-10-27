package org.example.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Identifiable<ID> {
    private ID id;

}
