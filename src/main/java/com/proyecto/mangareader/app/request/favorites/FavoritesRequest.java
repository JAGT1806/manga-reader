package com.proyecto.mangareader.app.request.favorites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoritesRequest {
    private String idManga;
    private Long userId;
    private String nameManga;
    private String urlImage;
}
