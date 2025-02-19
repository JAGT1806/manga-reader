package com.jagt1806.mangareader.dto.api;

import com.jagt1806.mangareader.dto.api.attributes.ChapterAttributes;
import lombok.Data;

@Data
public class Chapter {
    private String baseUrl;
    private ChapterAttributes chapter;
}
