package com.proyecto.mangareader.app.dto.out;

import com.proyecto.mangareader.app.dto.out.auxiliar.AuxFeedManga;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutFeedMangaDTO {
    private String id;
    private AuxFeedManga atributos;
}
