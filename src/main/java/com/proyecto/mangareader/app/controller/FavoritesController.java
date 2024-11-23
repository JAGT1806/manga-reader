package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.entity.FavoritesEntity;
import com.proyecto.mangareader.app.request.favorites.FavoritesRequest;
import com.proyecto.mangareader.app.responses.favorites.ListFavoritesResponse;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.service.IFavoritesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@Tag(name="Favoritos", description = "Información de los favoritos")
@RequiredArgsConstructor
public class FavoritesController {
    private final IFavoritesService favoritesService;

    @GetMapping
    public ResponseEntity<ListFavoritesResponse> getFavorites(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit) {
        ListFavoritesResponse response = favoritesService.getAllFavorites(offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListFavoritesResponse> getFavoritesByUser(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit) {
        ListFavoritesResponse response = favoritesService.getFavoritesByUserId(id, offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exist")
    public Boolean getFavoritesExist(@RequestParam Long id, @RequestParam String manga) {
        return favoritesService.existsFavorite(id, manga);
    }

    @PostMapping
    public ResponseEntity<FavoritesEntity> addFavorite(@RequestBody FavoritesRequest request) {
        FavoritesEntity response = favoritesService.createFavorite(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> deleteFavorite(@PathVariable Long id, @RequestParam String manga) {
        favoritesService.deleteFavoriteByIds(id, manga);
        return ResponseEntity.ok(new OkResponse());
    }

}
