package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.entity.FavoritesEntity;
import com.proyecto.mangareader.app.request.favorites.FavoritesRequest;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.responses.favorites.ListFavoritesResponse;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.service.IFavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar los favoritos de los usuarios en la aplicación Manga Reader.
 *
 * Este controlador proporciona endpoints para:
 * <ul>
 *   <li>Obtener lista de favoritos</li>
 *   <li>Obtener favoritos por usuario</li>
 *   <li>Verificar existencia de favorito</li>
 *   <li>Agregar favorito</li>
 *   <li>Eliminar favorito</li>
 * </ul>
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@RestController
@RequestMapping("/api/favorites")
@Tag(name="Favoritos", description = "Gestión de favoritos de usuarios")
@RequiredArgsConstructor
public class FavoritesController {
    /** Servicio para operaciones de favoritos. */
    private final IFavoritesService favoritesService;

    /**
     * Obtiene la lista de todos los favoritos con paginación.
     *
     * @param offset Número de página para la paginación (por defecto 0)
     * @param limit Número de elementos por página (por defecto 12)
     * @return Respuesta con la lista de favoritos
     */
    @Operation(summary = "Obtener lista de favoritos", description = "Recupera todos los favoritos con paginación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de favoritos obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListFavoritesResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    @GetMapping
    public ResponseEntity<ListFavoritesResponse> getFavorites(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit) {
        ListFavoritesResponse response = favoritesService.getAllFavorites(offset, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene los favoritos de un usuario específico.
     *
     * @param id Identificador del usuario
     * @param offset Número de página para la paginación (por defecto 0)
     * @param limit Número de elementos por página (por defecto 12)
     * @return Respuesta con la lista de favoritos del usuario
     */
    @Operation(summary = "Obtener favoritos por usuario", description = "Recupera los favoritos de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favoritos del usuario obtenidos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListFavoritesResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or @userSecurity.isUserAllowed(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<ListFavoritesResponse> getFavoritesByUser(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit) {
        ListFavoritesResponse response = favoritesService.getFavoritesByUserId(id, offset, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si un manga ya existe en favoritos de un usuario.
     *
     * @param id Identificador del usuario
     * @param manga Identificador del manga
     * @return Booleano indicando si el manga existe en favoritos
     */
    @Operation(summary = "Verificar existencia de favorito", description = "Comprueba si un manga está en favoritos de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Existencia verificada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/exist")
    public Boolean getFavoritesExist(@RequestParam Long id, @RequestParam String manga) {
        return favoritesService.existsFavorite(id, manga);
    }


    /**
     * Agrega un nuevo favorito para un usuario.
     *
     * @param request Detalles de la solicitud de favorito
     * @return Entidad de favorito creada
     */
    @Operation(summary = "Agregar favorito", description = "Crea un nuevo favorito para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorito creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FavoritesEntity.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto al crear favorito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<FavoritesEntity> addFavorite(@RequestBody FavoritesRequest request) {
        FavoritesEntity response = favoritesService.createFavorite(request);
        return ResponseEntity.ok(response);
    }


    /**
     * Elimina un favorito específico de un usuario.
     *
     * @param id Identificador del usuario
     * @param manga Identificador del manga a eliminar de favoritos
     * @return Respuesta de operación exitosa
     */
    @Operation(summary = "Eliminar favorito", description = "Elimina un favorito específico de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorito eliminado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "404", description = "Favorito no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> deleteFavorite(@PathVariable Long id, @RequestParam String manga) {
        favoritesService.deleteFavoriteByIds(id, manga);
        return ResponseEntity.ok(new OkResponse());
    }

}
