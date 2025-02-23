package com.jagt1806.mangareader.service;

import com.jagt1806.mangareader.http.request.favorite.FavoriteRequest;
import com.jagt1806.mangareader.http.response.favorite.FavoriteListResponse;

public interface FavoriteService {
    FavoriteListResponse getAllFavorites(int offset, int limit);

    FavoriteListResponse getFavoriteByUserId(Long userId, int offset, int limit);

    void addFavorite(Long id, FavoriteRequest request);

    void deleteFavoriteByIds(Long id, String mangaId);

    Boolean existsFavorite(Long userId, String mangaId);
}
