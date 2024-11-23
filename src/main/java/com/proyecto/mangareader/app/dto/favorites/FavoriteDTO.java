package com.proyecto.mangareader.app.dto.favorites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDTO {
    private Long id;
    private String idManga;
    private Long userId;
    private String nameManga;
    private String urlImage;

}
