package com.proyecto.mangareader.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Cliente Feign para interactuar con la API de MangaDex.
 *
 * Proporciona métodos para consultar: <br>
 * - Listado de mangas <br>
 * - Detalles de un manga específico <br>
 * - Capítulos de un manga <br>
 * - Información de alojamiento de capítulos <br>
 * <br><br>
 * Características: <br>
 * - Integración con Spring Cloud Feign <br>
 * - Configuración de URL base para MangaDex <br>
 * - Soporte de parámetros por defecto <br>
 * - Filtrado de contenido y lenguaje <br>
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@FeignClient(name = "userService", url = "https://api.mangadex.org")
public interface IMangaClient {
    /**
     * Recupera una lista de mangas con múltiples opciones de filtrado.
     *
     * @param title Título o parte del título para filtrar (opcional)
     * @param includes Recursos adicionales a incluir (ej. "cover_art")
     * @param offset Punto de inicio para la paginación (defecto: 0)
     * @param limit Número máximo de resultados (defecto: 12)
     * @param contentRating Filtro de clasificación de contenido (defecto: "safe")
     * @param availableTranslatedLanguage Idiomas de traducción disponibles (defecto: "es")
     * @return Mapa con los datos de los mangas recuperados
     */
    @GetMapping("/manga")
    Map<String, Object> getAllMangas(
            @RequestParam(required = false) String title,
            @RequestParam("includes[]") String includes,
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "12") int limit,
            @RequestParam(value="contentRating[]", defaultValue = "safe") List<String> contentRating,
            @RequestParam(value="availableTranslatedLanguage[]", defaultValue = "es") List<String> availableTranslatedLanguage);

    /**
     * Obtiene los detalles de un manga específico por su ID.
     *
     * @param id Identificador único del manga
     * @param includes Recursos adicionales a incluir (ej. "cover_art")
     * @return Mapa con los datos detallados del manga
     */
    @GetMapping("/manga/{id}")
    Map<String, Object> getManga(@PathVariable("id") String id, @RequestParam("includes[]") String includes);

    /**
     * Recupera la lista de capítulos de un manga con opciones de filtrado avanzadas.
     *
     * @param id Identificador del manga
     * @param offset Punto de inicio para la paginación (defecto: 0)
     * @param limit Número máximo de capítulos (defecto: 100)
     * @param contentRating Filtro de clasificación de contenido (defecto: "safe")
     * @param includeFutureUpdates Incluir actualizaciones futuras (defecto: 1)
     * @param volume Orden de volúmenes (defecto: ascendente)
     * @param chapter Orden de capítulos (defecto: ascendente)
     * @param availableTranslatedLanguage Idiomas de traducción disponibles (defecto: "es")
     * @return Mapa con los capítulos del manga
     */
    @GetMapping("/manga/{id}/feed")
    Map<String, Object> getMangaFeed(
            @PathVariable("id") String id,
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "100") int limit,
            @RequestParam(value="contentRating[]", defaultValue = "safe") List<String> contentRating,
            @RequestParam(value="includeFutureUpdates", defaultValue = "1") Byte includeFutureUpdates,
            @RequestParam(value="order[volume]", defaultValue = "asc") String volume,
            @RequestParam(value="order[chapter]", defaultValue = "asc") String chapter,
            @RequestParam(value="translatedLanguage[]", defaultValue = "es") List<String> availableTranslatedLanguage);

    /**
     * Obtiene la información de alojamiento de un capítulo de manga.
     *
     * @param id Identificador del capítulo
     * @return Mapa con la información de alojamiento del capítulo
     */
    @GetMapping("/at-home/server/{id}")
    Map<String, Object> getMangaAtHome(
            @PathVariable("id") String id
    );

}
