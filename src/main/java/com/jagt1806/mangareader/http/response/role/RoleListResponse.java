package com.jagt1806.mangareader.http.response.role;

import com.jagt1806.mangareader.model.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleListResponse {
    @Schema(example = "ok")
    private final String message = "ok";
    private List<Roles> data;
    private int offset;
    private int limit;
    private long total;
}