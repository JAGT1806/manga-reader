package com.proyecto.mangareader.app.responses.user;

import com.proyecto.mangareader.app.dto.out.OutUsersDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponse {
    @Schema(example = "ok")
    private final String message = "ok";
    private List<OutUsersDTO> data;
    private int offset;
    private int limit;
    private long total;
}