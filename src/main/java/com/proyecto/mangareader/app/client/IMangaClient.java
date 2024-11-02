package com.proyecto.mangareader.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "userService", url = "https://api.mangadex.org")
public interface IMangaClient {

    @GetMapping("/manga")
    Map<String, Object> getAllMangas(
            @RequestParam(required = false) String title,
            @RequestParam("includes[]") String includes,
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "10") int limit,
            @RequestParam(value="contentRating[]", defaultValue = "safe") List<String> contentRating);

    @GetMapping("/manga/{id}")
    Map<String, Object> getManga(@PathVariable("id") String id);

    @GetMapping("/manga/{id}/feed")
    Map<String, Object> getMangaFeed(
            @PathVariable("id") String id,
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "100") int limit,
            @RequestParam(value="contentRating[]", defaultValue = "safe") List<String> contentRating,
            @RequestParam(value="includeFutureUpdates", defaultValue = "1") Byte includeFutureUpdates,
            @RequestParam(value="order[volume]", defaultValue = "asc") String volume,
            @RequestParam(value="order[chapter]", defaultValue = "asc") String chapter);

    @GetMapping("/at-home/server/{id}")
    Map<String, Object> getMangaAtHome(
            @PathVariable("id") String id
    );

}
