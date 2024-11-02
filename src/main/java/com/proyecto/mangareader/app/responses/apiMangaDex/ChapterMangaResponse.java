package com.proyecto.mangareader.app.responses.apiMangaDex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterMangaResponse {
    private final String message = "Ok";
    private List<String> data;
    private List<String> dataSaver;
}
