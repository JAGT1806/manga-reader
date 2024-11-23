package com.proyecto.mangareader.app.responses.apiMangaDex;

import com.proyecto.mangareader.app.dto.mangas.FeedMangaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedMangaResponse {
    private final String message = "Ok";
    private List<FeedMangaDTO> data;
    private int limit;
    private int offset;
    private int total;
}
