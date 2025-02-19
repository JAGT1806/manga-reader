package com.jagt1806.mangareader.http.response.manga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterMangaResponse {
    private List<String> data;
    private List<String> dataSaver;
}
