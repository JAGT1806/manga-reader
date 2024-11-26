package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.client.IMangaClient;
import com.proyecto.mangareader.app.dto.mangas.FeedMangaDTO;
import com.proyecto.mangareader.app.dto.mangas.MangaDTO;
import com.proyecto.mangareader.app.dto.auxiliary.AuxFeedManga;
import com.proyecto.mangareader.app.responses.apiMangaDex.ChapterMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.FeedMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.ListMangasResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.MangaResponse;
import com.proyecto.mangareader.app.service.IMangaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar operaciones de manga.
 * Implementa la interfaz IMangaService para interactuar con un cliente de manga externo.
 *
 * Características principales:
 * - Filtrado de contenido por clasificación (seguro, sugerente, adulto)
 * - Soporte multilenguaje
 * - Conversión de respuestas de API a DTOs
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Service
@AllArgsConstructor
public class MangaService implements IMangaService {
    /** Cliente para interactuar con la API externa de manga. */
    private final IMangaClient imangaClient;

    /** Constante para incluir arte de portada en las solicitudes. */
    private static final String INCLUDES = "cover_art";

    /**
     * Lista mangas según los criterios de búsqueda proporcionados.
     *
     * @param tile Título o parte del título a buscar
     * @param offset Desplazamiento para paginación
     * @param limit Número máximo de resultados
     * @param nsfw Incluir contenido para adultos
     * @param language Idioma de los resultados
     * @return Lista de mangas que coinciden con los criterios
     */
    @Override
    public ListMangasResponse listMangas(String tile, int offset, int limit, boolean nsfw, String language) {
        List<String> contentRatingFilters = new ArrayList<>();
        contentRatingFilters.add("safe");
        contentRatingFilters.add("suggestive");

        if(nsfw) {
            contentRatingFilters.add("erotica");
            contentRatingFilters.add("pornographic");
        }

        List<String> availableTranslatedLanguage = new ArrayList<>();
        setAvailableTranslatedLanguage(language, availableTranslatedLanguage);

        Map<String, Object> params = imangaClient.getAllMangas(tile, INCLUDES, offset, limit, contentRatingFilters, availableTranslatedLanguage);

        return convertToListMangaResponse(params, offset, limit, language);
    }

    /**
     * Convierte la respuesta de la API en un objeto ListMangasResponse.
     *
     * @param params Respuesta de la API
     * @param offset Desplazamiento de paginación
     * @param limit Límite de resultados
     * @param language Idioma de los resultados
     * @return Respuesta de manga procesada
     */
    private ListMangasResponse convertToListMangaResponse(Map<String, Object> params, int offset, int limit, String language) {
        List<Map<String, Object>> mangaList = (List<Map<String, Object>>) params.get("data");

        List<MangaDTO> listMangaDTO = new ArrayList<>();

        for (Map<String, Object> manga : mangaList) {
            MangaDTO mangaDTO = new MangaDTO();
            mangaDTO.setId((String) manga.get("id"));

            Map<String, Object> attributes = (Map<String, Object>) manga.get("attributes");
            mangaDTO.setTitle((String) ((Map<String, Object>) attributes.get("title")).get("en"));
            mangaDTO.setDescription((String) ((Map<String, Object>) attributes.get("description")).get(language));

            List<Map<String, Object>> relationships = (List<Map<String, Object>>) manga.get("relationships");
            for (Map<String, Object> relationship : relationships) {
                if (INCLUDES.equals(relationship.get("type"))) {
                    mangaDTO.setCoverId((String) relationship.get("id"));
                    Map<String, Object> coverAttributes = (Map<String, Object>) relationship.get("attributes");
                    if (coverAttributes != null) {
                        mangaDTO.setFileName((String) coverAttributes.get("fileName"));
                    }
                    break;
                }
            }

            listMangaDTO.add(mangaDTO);
        }

        ListMangasResponse listMangasResponse = new ListMangasResponse();
        listMangasResponse.setData(listMangaDTO);
        listMangasResponse.setOffset(offset);
        listMangasResponse.setLimit(limit);

        Object totalObject = params.get("total"); // Extraemos total de la API

        listMangasResponse.setTotal((Integer) totalObject);

        return listMangasResponse;
    }

    /**
     * Obtiene los detalles de un manga específico por su ID.
     *
     * @param id Identificador único del manga
     * @param language Idioma de los detalles
     * @return Detalles completos del manga
     */
    @Override
    public MangaResponse getManga(String id, String language) {
        Map<String, Object> params = imangaClient.getManga(id, INCLUDES);
        return convertToMangaResponse(params, language);
    }

    /**
     * Convierte la respuesta de la API de un manga en un objeto MangaResponse.
     *
     * @param params Respuesta de la API
     * @param language Idioma de los detalles
     * @return Respuesta de manga procesada
     */
    public MangaResponse convertToMangaResponse(Map<String, Object> params, String language) {
        Map<String, Object> manga = (Map<String, Object>) params.get("data");

        MangaDTO mangaDTO = new MangaDTO();

        mangaDTO.setId((String) manga.get("id"));

        Map<String, Object> attributes = (Map<String, Object>) manga.get("attributes");
        mangaDTO.setTitle((String) ((Map<String, Object>) attributes.get("title")).get("en"));
        mangaDTO.setDescription((String) ((Map<String, Object>) attributes.get("description")).get(language));

        List<Map<String, Object>> relationships = (List<Map<String, Object>>) manga.get("relationships");
        for (Map<String, Object> relationship : relationships) {
            if (INCLUDES.equals(relationship.get("type"))) {
                mangaDTO.setCoverId((String) relationship.get("id"));
                Map<String, Object> coverAttributes = (Map<String, Object>) relationship.get("attributes");
                if (coverAttributes != null) {
                    mangaDTO.setFileName((String) coverAttributes.get("fileName"));
                }
                break;
            }
        }

        MangaResponse mangaResponse = new MangaResponse();
        mangaResponse.setData(mangaDTO);

        return mangaResponse;
    }


    /**
     * Busca los capítulos (feed) de un manga específico.
     *
     * @param id Identificador del manga
     * @param offset Desplazamiento para paginación
     * @param limit Número máximo de capítulos
     * @param nsfw Incluir contenido para adultos
     * @param language Idioma de los capítulos
     * @return Feed de capítulos del manga
     */
    @Override
    public FeedMangaResponse searchFeed(String id, int offset, int limit, boolean nsfw, String language) {
        List<String> contentRatingFilters = new ArrayList<>();
        contentRatingFilters.add("safe");
        contentRatingFilters.add("suggestive");

        if(nsfw) {
            contentRatingFilters.add("erotica");
            contentRatingFilters.add("pornographic");
        }

        List<String> availableTranslatedLanguage = new ArrayList<>();
        setAvailableTranslatedLanguage(language, availableTranslatedLanguage);

        Map<String, Object> params = imangaClient.getMangaFeed(id, offset, limit, contentRatingFilters, null, "asc", "asc", availableTranslatedLanguage);

        return convertToFeedMangaResponse(params, offset, limit);
    }

    /**
     * Convierte la respuesta de la API del feed de manga en un objeto FeedMangaResponse.
     *
     * @param params Respuesta de la API
     * @param offset Desplazamiento de paginación
     * @param limit Límite de resultados
     * @return Respuesta de feed de manga procesada
     */
    private FeedMangaResponse convertToFeedMangaResponse(Map<String, Object> params, int offset, int limit) {
        List<Map<String, Object>> feedManga = (List<Map<String, Object>>) params.get("data");

        List<FeedMangaDTO> listFeedMangaDTO = new ArrayList<>();

        for (Map<String, Object> feed : feedManga) {
            FeedMangaDTO feedMangaDTO = new FeedMangaDTO();
            AuxFeedManga aux = new AuxFeedManga();

            feedMangaDTO.setId((String) feed.get("id"));

            Map<String, Object> attributes = (Map<String, Object>) feed.get("attributes");

            aux.setVolumen((String) attributes.get("volume"));
            aux.setCapitulo((String) attributes.get("chapter"));
            aux.setTitulo((String) attributes.get("title"));
            aux.setIdioma((String) attributes.get("translatedLanguage"));
            aux.setPaginas((Integer) attributes.get("pages"));

            feedMangaDTO.setAtributos(aux);

            listFeedMangaDTO.add(feedMangaDTO);
        }

        FeedMangaResponse feedMangaResponse = new FeedMangaResponse();
        feedMangaResponse.setData(listFeedMangaDTO);
        feedMangaResponse.setOffset(offset);
        feedMangaResponse.setLimit(limit);

        Object totalObject = params.get("total"); // Extraemos total de la API

        feedMangaResponse.setTotal((Integer) totalObject);

        return feedMangaResponse;
    }

    /**
     * Configura los idiomas disponibles para la traducción.
     * Añade soporte específico para español latino.
     *
     * @param language Idioma principal
     * @param availableTranslatedLanguage Lista de idiomas disponibles
     */
    private void setAvailableTranslatedLanguage(String language, List<String> availableTranslatedLanguage) {
        availableTranslatedLanguage.add(language);

        if(language.equals("es")) {
            availableTranslatedLanguage.add("es-la");
        }

    }


    /**
     * Obtiene las URLs de las páginas de un capítulo de manga.
     *
     * @param id Identificador del capítulo
     * @return Respuesta con URLs de páginas del capítulo
     */

    @Override
    public ChapterMangaResponse getChapterManga(String id) {
        Map<String, Object> params = imangaClient.getMangaAtHome(id);
        return convertToChapterMangaResponse(params);
    }

    /**
     * Convierte la respuesta de la API de un capítulo en un objeto ChapterMangaResponse.
     * Genera URLs completas para las imágenes del capítulo.
     *
     * @param params Respuesta de la API con datos del capítulo
     * @return Respuesta de capítulo procesada con URLs de páginas
     */
    public ChapterMangaResponse convertToChapterMangaResponse(Map<String, Object> params) {
        ChapterMangaResponse chapterMangaResponse = new ChapterMangaResponse();

        String baseUrl = (String) params.get("baseUrl");
        Map<String, Object> chapterManga = (Map<String, Object>) params.get("chapter");
        String chapterHash = (String) chapterManga.get("hash");

        // Guardar los datos del campo data
        List<String> data = new ArrayList<>();
        List<String> dataApi = (List<String>) chapterManga.get("data");

        for (String dataManga : dataApi) {
            String url = baseUrl + "/data/" + chapterHash + "/" + dataManga;
            data.add(url);
        }

        chapterMangaResponse.setData(data);

        // Almacenar dataSaver
        data = new ArrayList<>();
        dataApi = (List<String>) chapterManga.get("dataSaver");
        for (String dataManga : dataApi) {
            String url = baseUrl + "/data-saver/" + chapterHash + "/" + dataManga;
            data.add(url);
        }
        chapterMangaResponse.setDataSaver(data);

        return chapterMangaResponse;
    }
}
