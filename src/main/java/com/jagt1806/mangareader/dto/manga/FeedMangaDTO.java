package com.jagt1806.mangareader.dto.manga;

import com.jagt1806.mangareader.dto.manga.aux.AuxFeedManga;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedMangaDTO {
    private String id;
    private AuxFeedManga attributes;
}