package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.client.IMangaClient;
import com.proyecto.mangareader.app.dto.out.OutChapterMangaDTO;
import com.proyecto.mangareader.app.dto.out.OutMangaDTO;
import com.proyecto.mangareader.app.responses.apiMangaDex.ListMangasResponse;
import com.proyecto.mangareader.app.service.IMangaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
public class MangaService implements IMangaService {
    private final IMangaClient imangaClient;
    private static final String INCLUDES = "cover_art";

    @Override
    public ListMangasResponse listMangas(String tile, int offset, int limit) {
        Map<String, Object> params = imangaClient.getAllMangas(tile, INCLUDES, offset, limit);

        return convertToListMangaResponse(params, offset, limit);
    }

    private ListMangasResponse convertToListMangaResponse(Map<String, Object> params, int offset, int limit) {
        List<Map<String, Object>> mangaList = (List<Map<String, Object>>) params.get("data");

        List<OutMangaDTO> listMangaDTO = new ArrayList<>();

        for (Map<String, Object> manga : mangaList) {
            OutMangaDTO mangaDTO = new OutMangaDTO();
            mangaDTO.setId((String) manga.get("id"));

            Map<String, Object> attributes = (Map<String, Object>) manga.get("attributes");
            mangaDTO.setTitle((String) attributes.get("title"));
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


    public List<OutChapterMangaDTO> searchFeed(String id) {
        List<OutChapterMangaDTO> chapterMangaDTOList = new ArrayList<>();
        return chapterMangaDTOList;
    }
}
