package com.proyecto.mangareader.app.responses.favorites;

import com.proyecto.mangareader.app.entity.FavoritesEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {
    private final String message = "Ok";
    private FavoritesEntity data;
}
