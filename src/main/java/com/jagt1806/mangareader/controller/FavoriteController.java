package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.http.request.favorite.FavoriteRequest;
import com.jagt1806.mangareader.http.response.error.ErrorResponse;
import com.jagt1806.mangareader.http.response.favorite.FavoriteListResponse;
import com.jagt1806.mangareader.http.response.ok.OkResponse;
import com.jagt1806.mangareader.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
@SecurityRequirement(name = "Auth")
@Tag(name = "Favorites", description = "Buscar información de los favoritos")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(summary = "Obtener lista de favoritos", description = "Recupera todos los favoritos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de favoritos obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('${app.admin.role}')")
    @GetMapping
    public ResponseEntity<FavoriteListResponse> getFavorites(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit
    ) {
        FavoriteListResponse response = favoriteService.getAllFavorites(offset, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener lista de favoritos por usuario", description = "Recupera todos los favoritos de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de favoritos obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteListResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('${app.admin.role}') or @userSecurity.isUserAllowed(#userId)")
    @GetMapping("/{userId}/user")
    public ResponseEntity<FavoriteListResponse> getFavoritesByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit
    ) {
        FavoriteListResponse response = favoriteService.getFavoriteByUserId(userId, offset, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verificar existencia del favorito del usuario", description = "Comprueba si un manga está en favoritos de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de favoritos obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('${app.admin.role}') or @userSecurity.isUserAllowed(#userId)")
    @GetMapping("/{userId}/exist/{idManga}")
    public ResponseEntity<Boolean> existFavorite(@PathVariable Long userId, @PathVariable String idManga) {
        Boolean response = favoriteService.existsFavorite(userId, idManga);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Agregar favorito", description = "Crea un nuevo manga favorito para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga añadido exitosamente a favoritos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('${app.admin.role}') or @userSecurity.isUserAllowed(#userId)")
    @PostMapping("/{userId}/add")
    public ResponseEntity<OkResponse> addFavorite(@PathVariable Long userId, @RequestBody FavoriteRequest request) {
        favoriteService.addFavorite(userId, request);
        return new ResponseEntity<>(new OkResponse(), HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar favorito", description = "Elimina un manga favorito de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga favorito eliminado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "404", description = "Manga favorito del usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('${app.admin.role}') or @userSecurity.isUserAllowed(#userId)")
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<OkResponse> deleteFavorite(@PathVariable Long userId, @RequestParam String mangaId) {
        favoriteService.deleteFavoriteByIds(userId, mangaId);
        return ResponseEntity.ok(new OkResponse());
    }
}
