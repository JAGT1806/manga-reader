package com.proyecto.mangareader.app.responses.apiMangaDex;

import com.proyecto.mangareader.app.dto.mangas.MangaDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaResponse {
    @Schema(example = "ok")
    private final String message = "ok";
    private MangaDTO data;
}
