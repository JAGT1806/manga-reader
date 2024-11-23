package com.proyecto.mangareader.app.responses.favorites;

import com.proyecto.mangareader.app.dto.favorites.FavoriteDTO;
import com.proyecto.mangareader.app.entity.FavoritesEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListFavoritesResponse {
    private final String message = "Ok";
    private List<FavoriteDTO> data;
    private int offset;
    private int limit;
    private long total;
}
