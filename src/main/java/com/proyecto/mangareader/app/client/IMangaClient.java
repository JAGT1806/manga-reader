package com.proyecto.mangareader.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "userService", url = "https://api.mangadex.org/manga")
public interface IMangaClient {

    @GetMapping("/{id}/feed")
    Map<String, Object> getMangaFeed(
            @PathVariable("id") String id,
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "10") int limit);

    @GetMapping
    Map<String, Object> getAllMangas(
            @RequestParam(required = false) String title,
            @RequestParam("includes[]") String includes,
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "10") int limit
    );

}
