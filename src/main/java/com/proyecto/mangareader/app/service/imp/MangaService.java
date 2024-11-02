package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.client.IMangaClient;
import com.proyecto.mangareader.app.dto.out.OutFeedMangaDTO;
import com.proyecto.mangareader.app.dto.out.OutMangaDTO;
import com.proyecto.mangareader.app.dto.out.auxiliar.AuxFeedManga;
import com.proyecto.mangareader.app.responses.apiMangaDex.ChapterMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.FeedMangaResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.ListMangasResponse;
import com.proyecto.mangareader.app.responses.apiMangaDex.MangaResponse;
import com.proyecto.mangareader.app.service.IMangaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
public class MangaService implements IMangaService {
    private final IMangaClient imangaClient;
    private static final String INCLUDES = "cover_art";

    @Override
    public ListMangasResponse listMangas(String tile, int offset, int limit, boolean nsfw) {
        List<String> contentRatingFilters = new ArrayList<>();
        contentRatingFilters.add("safe");
        contentRatingFilters.add("suggestive");

        if(nsfw) {
            contentRatingFilters.add("erotica");
            contentRatingFilters.add("pornographic");
        }
        Map<String, Object> params = imangaClient.getAllMangas(tile, INCLUDES, offset, limit, contentRatingFilters);

        return convertToListMangaResponse(params, offset, limit);
    }

    private ListMangasResponse convertToListMangaResponse(Map<String, Object> params, int offset, int limit) {
        List<Map<String, Object>> mangaList = (List<Map<String, Object>>) params.get("data");

        List<OutMangaDTO> listMangaDTO = new ArrayList<>();

        for (Map<String, Object> manga : mangaList) {
            OutMangaDTO mangaDTO = new OutMangaDTO();
            mangaDTO.setId((String) manga.get("id"));

            Map<String, Object> attributes = (Map<String, Object>) manga.get("attributes");
            mangaDTO.setTitle((String) ((Map<String, Object>) attributes.get("title")).get("en"));
            mangaDTO.setDescription((Map<String, Object>) attributes.get("description"));

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

    @Override
    public MangaResponse getManga(String id) {
        Map<String, Object> params = imangaClient.getManga(id);
        return convertToMangaResponse(params);
    }

    public MangaResponse convertToMangaResponse(Map<String, Object> params) {
        Map<String, Object> manga = (Map<String, Object>) params.get("data");

        OutMangaDTO mangaDTO = new OutMangaDTO();

        mangaDTO.setId((String) manga.get("id"));

        Map<String, Object> attributes = (Map<String, Object>) manga.get("attributes");
        mangaDTO.setTitle((String) ((Map<String, Object>) attributes.get("title")).get("en"));
        mangaDTO.setDescription((Map<String, Object>) attributes.get("description"));

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



    @Override
    public FeedMangaResponse searchFeed(String id, int offset, int limit, boolean nsfw) {
        List<String> contentRatingFilters = new ArrayList<>();
        contentRatingFilters.add("safe");
        contentRatingFilters.add("suggestive");

        if(nsfw) {
            contentRatingFilters.add("erotica");
            contentRatingFilters.add("pornographic");
        }
        Map<String, Object> params = imangaClient.getMangaFeed(id, offset, limit, contentRatingFilters, null, "asc", "asc");

        return convertToFeedMangaResponse(params, offset, limit);
    }

    private FeedMangaResponse convertToFeedMangaResponse(Map<String, Object> params, int offset, int limit) {
        List<Map<String, Object>> feedManga = (List<Map<String, Object>>) params.get("data");

        List<OutFeedMangaDTO> listFeedMangaDTO = new ArrayList<>();

        for (Map<String, Object> feed : feedManga) {
            OutFeedMangaDTO feedMangaDTO = new OutFeedMangaDTO();
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

    @Override
    public ChapterMangaResponse getChapterManga(String id) {
        Map<String, Object> params = imangaClient.getMangaAtHome(id);
        return convertToChapterMangaResponse(params);
    }

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
