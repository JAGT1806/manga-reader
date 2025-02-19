package com.jagt1806.mangareader.http.response.manga;

import com.jagt1806.mangareader.dto.manga.MangaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MangaResponse {
    private MangaDTO data;
}
