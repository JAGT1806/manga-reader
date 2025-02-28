package com.jagt1806.mangareader.dto.manga.auxiliary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuxFeedManga {
    private String volume;
    private String chapter;
    private String title;
    private String language;
    private int pages;
}