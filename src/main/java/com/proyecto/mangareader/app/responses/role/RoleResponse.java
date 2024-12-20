package com.proyecto.mangareader.app.responses.role;

import com.proyecto.mangareader.app.entity.RolesEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    @Schema(example = "ok")
    private final String message = "ok";
    private RolesEntity data; // Es la data muestra al usuario o usuarios
}
