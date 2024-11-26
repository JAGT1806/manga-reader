package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.responses.apiMangaDex.ChapterMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.FeedMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.ListMangasResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.MangaResponse;

/**
 * Interfaz de servicio para gestionar operaciones relacionadas con manga.
 * Proporciona métodos para recuperar información de manga desde una API externa.
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
public interface IMangaService {
    /**
     * Recupera una lista de mangas según criterios de búsqueda específicos.
     *
     * @param tile Título o título parcial del manga a buscar
     * @param offset Desplazamiento de paginación para el conjunto de resultados
     * @param limit Número máximo de entradas de manga a devolver
     * @param nsfw Indicador para incluir contenido para adultos (calificaciones eróticas/pornográficas)
     * @param language El idioma preferido para títulos y descripciones de manga
     * @return Una respuesta ListMangasResponse que contiene datos de manga, recuento total, desplazamiento y límite
     */
    ListMangasResponse listMangas(String tile, int offset, int limit, boolean nsfw, String language);

    /**
     * Recupera información detallada sobre un manga específico por su ID.
     *
     * @param id El identificador único del manga
     * @param language El idioma preferido para el título y descripción del manga
     * @return Una respuesta MangaResponse que contiene información detallada del manga
     */
    MangaResponse getManga(String id, String language);

    /**
     * Busca y recupera la fuente (capítulos) de un manga específico.
     *
     * @param id El identificador único del manga
     * @param offset Desplazamiento de paginación para el conjunto de resultados
     * @param limit Número máximo de capítulos a devolver
     * @param nsfw Indicador para incluir contenido para adultos (calificaciones eróticas/pornográficas)
     * @param language El idioma preferido para los capítulos del manga
     * @return Una respuesta FeedMangaResponse que contiene información de capítulos de manga
     */
    FeedMangaResponse searchFeed(String id, int offset, int limit, boolean nsfw, String language);

    /**
     * Recupera los datos del capítulo y las URL de imágenes para un capítulo de manga específico.
     *
     * @param id El identificador único del capítulo de manga
     * @return Una respuesta ChapterMangaResponse que contiene las URL de imágenes para el capítulo
     */
    ChapterMangaResponse getChapterManga(String id);
}
