package com.proyecto.mangareader.app.controller;


import com.proyecto.mangareader.app.responses.apiMangaDex.ChapterMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.FeedMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.ListMangasResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.MangaResponse;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.service.IMangaService;
import com.proyecto.mangareader.app.service.imp.MangaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador REST para la gestión de información de mangas.
 *
 * Proporciona endpoints para buscar y recuperar información de mangas
 * utilizando la API de MangaDex.
 *
 * @see MangaService
 */
@RestController
@RequestMapping("/api/manga")
@AllArgsConstructor
@Tag(name="Mangas", description = "Buscar información de mangas de MangaDex API")
public class MangaController {
    /** Servicio para la gestión de operaciones de mangas. */
    private final IMangaService mangaService;

    /**
     * Recupera una lista de mangas con opciones de filtrado y paginación.
     *
     * @param title Título parcial para filtrar mangas (opcional)
     * @param offset Número de página para la paginación (por defecto 0)
     * @param limit Número máximo de resultados por página (por defecto 100)
     * @param nsfw Indica si se incluyen contenidos para adultos (por defecto false)
     * @param language Idioma para los resultados (por defecto español)
     * @return Lista de mangas que coinciden con los criterios de búsqueda
     */
    @Operation(summary = "Obtener todos los mangas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListMangasResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ListMangasResponse getAllMangas(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit,
            @RequestParam(required = false, defaultValue = "false") boolean nsfw,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
        return mangaService.listMangas(title, offset, limit, nsfw, language);
    }

    /**
     * Recupera los detalles de un manga específico por su identificador.
     *
     * @param id Identificador único del manga
     * @param language Idioma para los resultados (por defecto español)
     * @return Detalles completos del manga solicitado
     */
    @Operation(summary = "Obtener un manga por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MangaResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public MangaResponse getMangaById(@PathVariable String id, @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
        return mangaService.getManga(id, language);
    }

    /**
     * Recupera el feed (capítulos) de un manga específico.
     *
     * @param id Identificador único del manga
     * @param offset Número de página para la paginación (por defecto 0)
     * @param limit Número máximo de resultados por página (por defecto 10)
     * @param nsfw Indica si se incluyen contenidos para adultos (por defecto false)
     * @param language Idioma para los resultados (por defecto español)
     * @return Lista de capítulos del manga
     */
    @Operation(summary = "Obtener todos los mangas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeedMangaResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/feed")
    public FeedMangaResponse getFeedMangas(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "100") int limit,
            @RequestParam(required = false, defaultValue = "false") boolean nsfw,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
        return mangaService.searchFeed(id, offset, limit, nsfw, language);
    }

    /**
     * Recupera los detalles de un capítulo específico.
     *
     * @param id Identificador único del capítulo
     * @return Detalles completos del capítulo solicitado
     */
    @Operation(summary = "Obtener detalles de un capítulo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterMangaResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/chapter/{id}")
    public ChapterMangaResponse getChapterMangas(@PathVariable String id) {
        return mangaService.getChapterManga(id);
    }

}
