package com.proyecto.mangareader.app.controller;


import com.proyecto.mangareader.app.responses.apiMangaDex.ChapterMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.FeedMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.ListMangasResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.MangaResponse;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
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


@RestController
@RequestMapping("/api/manga")
@AllArgsConstructor
@Tag(name="Mangas", description = "Buscar información de mangas de MangaDex API")
@CrossOrigin(origins = "http://localhost:4200")
public class MangaController {
    private final MangaService mangaService;

    @Operation(summary = "Obtener todos los mangas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = ListMangasResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ListMangasResponse getAllMangas(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "100") int limit,
            @RequestParam(required = false, defaultValue = "false") boolean nsfw,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
        return mangaService.listMangas(title, offset, limit, nsfw, language);
    }

    @GetMapping("/{id}")
    public MangaResponse getMangaById(@PathVariable String id, @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
        return mangaService.getManga(id, language);
    }

    @Operation(summary = "Obtener todos los mangas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manga encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = ListMangasResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content=@Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/feed")
    public FeedMangaResponse getFeedMangas(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "false") boolean nsfw,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
        return mangaService.searchFeed(id, offset, limit, nsfw, language);
    }

    @GetMapping("/chapter/{id}")
    public ChapterMangaResponse getChapterMangas(@PathVariable String id) {
        return mangaService.getChapterManga(id);
    }

}
