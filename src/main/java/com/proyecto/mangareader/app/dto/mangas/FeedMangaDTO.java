package com.proyecto.mangareader.app.dto.mangas;

import com.proyecto.mangareader.app.dto.auxiliary.AuxFeedManga;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeedMangaDTO {
    private String id;
    private AuxFeedManga atributos;
}
