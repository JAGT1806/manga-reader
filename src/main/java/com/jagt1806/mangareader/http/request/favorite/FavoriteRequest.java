package com.jagt1806.mangareader.http.request.favorite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest {
    private String idManga;
    private String nameManga;
    private String urlImage;
}
