package com.jagt1806.mangareader.client;

import com.jagt1806.mangareader.dto.api.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "manga-service", url = "https://api.mangadex.org")
public interface MangaClient {
    @GetMapping("/manga")
    MangaList getSearchManga(
            @RequestParam(required = false) String title,
            @RequestParam("includes[]") String includes,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "12") int limit,
            @RequestParam(value = "contentRating[]", defaultValue = "safe")List<String> contentRating,
            @RequestParam(value = "availableTranslatedLanguage[]", defaultValue = "es") List<String> language
    );

    @GetMapping("/manga/{id}")
    Manga getMangaId(
            @PathVariable("id") String id,
            @RequestParam("includes[]") String include
    );

    @GetMapping("/manga/{id}/feed")
    FeedList getMangaIdFeed(
            @PathVariable("id") String id,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "12") int limit,
            @RequestParam(value = "contentRating[]", defaultValue = "safe") List<String> contentRating,
            @RequestParam(value = "includeFutureUpdates", defaultValue = "1") Byte includeFutureUpdates,
            @RequestParam(value = "order[volume]", defaultValue = "asc") String volume,
            @RequestParam(value = "order[chapter]", defaultValue = "asc") String chapter,
            @RequestParam(value = "translatedLanguage[]", defaultValue = "es") List<String> language
    );

    @GetMapping("/at-home/server/{chapterId}")
    Chapter getAtHomeServerChapterId(
            @PathVariable("chapterId") String chapterId
    );
}