package com.jagt1806.mangareader.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaList {
    private List<MangaData> data;
    private int offset;
    private int limit;
    private long total;
}
