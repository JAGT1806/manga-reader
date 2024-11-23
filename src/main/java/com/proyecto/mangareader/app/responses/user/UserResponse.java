package com.proyecto.mangareader.app.responses.user;

import com.proyecto.mangareader.app.dto.users.UsersDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @Schema(example = "ok")
    private final String message = "ok";
    private UsersDTO data; // En la data muestra al usuario o usuarios
}
