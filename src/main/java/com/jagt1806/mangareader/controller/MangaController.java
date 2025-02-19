package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.http.response.manga.ChapterMangaResponse;
import com.jagt1806.mangareader.http.response.manga.FeedMangaResponse;
import com.jagt1806.mangareader.http.response.manga.MangaListResponse;
import com.jagt1806.mangareader.http.response.manga.MangaResponse;
import com.jagt1806.mangareader.service.MangaService;
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

    @GetMapping
    public ResponseEntity<MangaListResponse> getMangas(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "12") int limit,
            @RequestParam(required = false, defaultValue = "false") boolean nsfw,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String language
    ) {
        MangaListResponse response = mangaService.getMangas(title, offset, limit, nsfw, language);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MangaResponse> getMangaById(
            @PathVariable String id,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String language) {
        MangaResponse response = mangaService.getMangaId(id, language);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/feed")
    public ResponseEntity<FeedMangaResponse> searchFeed(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "100") int limit,
            @RequestParam(required = false, defaultValue = "false") boolean nsfw,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String language
    ) {
        FeedMangaResponse response = mangaService.getFeed(id, offset, limit, nsfw, language);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chapter/{idChapter}")
    public ResponseEntity<ChapterMangaResponse> getChapter(@PathVariable String idChapter) {
        ChapterMangaResponse response = mangaService.getChapter(idChapter);
        return ResponseEntity.ok(response);
    }

}