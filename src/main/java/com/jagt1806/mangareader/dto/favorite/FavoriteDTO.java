package com.jagt1806.mangareader.dto.favorite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Long id;
    private String idManga;
    private Long userId;
    private String nameManga;
    private String urlImage;
}
