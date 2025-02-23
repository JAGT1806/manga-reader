package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.http.request.favorite.FavoriteRequest;
import com.jagt1806.mangareader.http.response.favorite.FavoriteListResponse;
import com.jagt1806.mangareader.http.response.ok.OkResponse;
import com.jagt1806.mangareader.service.FavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
@Tag(name = "Favorites", description = "Buscar información de los favoritos")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<FavoriteListResponse> getFavorites(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit
    ) {
        FavoriteListResponse response = favoriteService.getAllFavorites(offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/user")
    public ResponseEntity<FavoriteListResponse> getFavoritesByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit
    ) {
        FavoriteListResponse response = favoriteService.getFavoriteByUserId(userId, offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/exist/{idManga}")
    public ResponseEntity<Boolean> existFavorite(@PathVariable Long userId, @PathVariable String idManga) {
        Boolean response = favoriteService.existsFavorite(userId, idManga);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<OkResponse> addFavorite(@PathVariable Long userId, @RequestBody FavoriteRequest request) {
        favoriteService.addFavorite(userId, request);
        return new ResponseEntity<>(new OkResponse(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<OkResponse> deleteFavorite(@PathVariable Long id, @RequestParam String mangaId) {
        favoriteService.deleteFavoriteByIds(id, mangaId);
        return ResponseEntity.ok(new OkResponse());
    }
}
