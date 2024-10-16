package com.proyecto.mangareader.app.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleResponse {
    private Long id;
    private String name;
    private String message;
}
