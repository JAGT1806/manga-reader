package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.responses.apiMangaDex.ListMangasResponse;


public interface IMangaService {
    ListMangasResponse listMangas(String tile, int offset, int limit);
}
