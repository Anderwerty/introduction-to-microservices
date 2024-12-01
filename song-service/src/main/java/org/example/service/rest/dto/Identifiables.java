package org.example.service.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Identifiables<ID> {
    private List<ID> ids;
}
