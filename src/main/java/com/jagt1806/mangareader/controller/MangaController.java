package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.http.response.error.ErrorResponse;
import com.jagt1806.mangareader.http.response.manga.ChapterMangaResponse;
import com.jagt1806.mangareader.http.response.manga.FeedMangaResponse;
import com.jagt1806.mangareader.http.response.manga.MangaListResponse;
import com.jagt1806.mangareader.http.response.manga.MangaResponse;
import com.jagt1806.mangareader.service.MangaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manga")
@Tag(name = "Mangas", description = "Buscar información de MangaDex API")
public class MangaController {
  private final MangaService mangaService;

  @Operation(summary = "Obtener mangas", description = "Obtiene la información de los mangas de la API de MangaDex")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Mangas encontrados exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MangaListResponse.class))),
      @ApiResponse(responseCode = "502", description = "Error con la conexión de la API", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping
  public ResponseEntity<MangaListResponse> getMangas(
      @RequestParam(required = false) String title,
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "12") int limit,
      @RequestParam(required = false, defaultValue = "false") boolean nsfw,
      @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
    MangaListResponse response = mangaService.getMangas(title, offset, limit, nsfw, language);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Obtener manga por id", description = "Obtiene la información del manga por id de la API de MangaDex")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Manga encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MangaResponse.class))),
      @ApiResponse(responseCode = "502", description = "Error con la conexión de la API", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<MangaResponse> getMangaById(
      @PathVariable String id,
      @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
    MangaResponse response = mangaService.getMangaId(id, language);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Obtener el contenido de un manga", description = "Obtiene los capítulos y volúmenes de los mangas de la API de MangaDex")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Contenido traído exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeedMangaResponse.class))),
      @ApiResponse(responseCode = "502", description = "Error con la conexión de la API", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{id}/feed")
  public ResponseEntity<FeedMangaResponse> searchFeed(
      @PathVariable String id,
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "100") int limit,
      @RequestParam(required = false, defaultValue = "false") boolean nsfw,
      @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
    FeedMangaResponse response = mangaService.getFeed(id, offset, limit, nsfw, language);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Obtener contenido de un capítulo", description = "Obtiene las imágenes de un capítulo de un manga de la API de MangaDex")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Datos encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChapterMangaResponse.class))),
      @ApiResponse(responseCode = "502", description = "Error con la conexión de la API", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/chapter/{idChapter}")
  public ResponseEntity<ChapterMangaResponse> getChapter(@PathVariable String idChapter) {
    ChapterMangaResponse response = mangaService.getChapter(idChapter);
    return ResponseEntity.ok(response);
  }

}
