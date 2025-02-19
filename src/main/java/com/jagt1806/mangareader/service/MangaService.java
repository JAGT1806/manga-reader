package com.jagt1806.mangareader.service;

import com.jagt1806.mangareader.http.response.manga.ChapterMangaResponse;
import com.jagt1806.mangareader.http.response.manga.FeedMangaResponse;
import com.jagt1806.mangareader.http.response.manga.MangaListResponse;
import com.jagt1806.mangareader.http.response.manga.MangaResponse;

public interface MangaService {
    MangaListResponse getMangas(String title, int offset, int limit, boolean nsfw, String language);

    MangaResponse getMangaId(String id, String language);

    FeedMangaResponse getFeed(String id, int offset, int limit, boolean nsfw, String language);

    ChapterMangaResponse getChapter(String id);
}