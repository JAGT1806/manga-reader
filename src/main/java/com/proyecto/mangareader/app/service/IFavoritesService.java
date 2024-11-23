package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.FavoritesEntity;
import com.proyecto.mangareader.app.request.favorites.FavoritesRequest;
import com.proyecto.mangareader.app.responses.favorites.ListFavoritesResponse;

public interface IFavoritesService {
    ListFavoritesResponse getAllFavorites(int offset, int limit);
    FavoritesEntity getFavoriteById(Long id);
    ListFavoritesResponse getFavoritesByUserId(Long userId, int offset, int limit);
    void deleteFavoriteByIds(Long id, String mangaId);
    FavoritesEntity createFavorite(FavoritesRequest favorite);
    Boolean existsFavorite(Long userid, String mangaId);
}
