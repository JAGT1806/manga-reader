package com.proyecto.mangareader.app.responses.role;

import com.proyecto.mangareader.app.entity.RolesEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleListResponse {
    @Schema(example = "ok")
    private final String message = "ok";
    private List<RolesEntity> data;
    private int offset;
    private int limit;
    private long total;
}
