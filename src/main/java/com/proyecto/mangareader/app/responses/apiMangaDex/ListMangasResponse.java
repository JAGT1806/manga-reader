package com.proyecto.mangareader.app.responses.apiMangaDex;

import com.proyecto.mangareader.app.dto.mangas.MangaDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListMangasResponse {
    @Schema(example = "ok")
    private final String message = "ok";
    private List<MangaDTO> data;
    private int offset;
    private int limit;
    private long total;
}
