package com.jagt1806.mangareader.http.response.manga;

import com.jagt1806.mangareader.dto.manga.MangaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MangaListResponse {
    private List<MangaDTO> data;
    private int offset;
    private int limit;
    private long total;
}