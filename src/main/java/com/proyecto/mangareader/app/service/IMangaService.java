package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.responses.apiMangaDex.ChapterMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.FeedMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.ListMangasResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.MangaResponse;


public interface IMangaService {
    ListMangasResponse listMangas(String tile, int offset, int limit, boolean nsfw);

    MangaResponse getManga(String id);

    FeedMangaResponse searchFeed(String id, int offset, int limit, boolean nsfw);

    ChapterMangaResponse getChapterManga(String id);


}
