package com.jagt1806.mangareader.dto.manga.aux;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuxFeedManga {
    private String volume;
    private String chapter;
    private String tittle;
    private String language;
    private int pages;
}
