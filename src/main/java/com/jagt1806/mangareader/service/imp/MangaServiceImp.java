package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.client.MangaClient;
import com.jagt1806.mangareader.dto.api.attributes.*;
import com.jagt1806.mangareader.dto.manga.FeedMangaDTO;
import com.jagt1806.mangareader.dto.manga.MangaDTO;
import com.jagt1806.mangareader.dto.api.*;
import com.jagt1806.mangareader.dto.manga.aux.AuxFeedManga;
import com.jagt1806.mangareader.http.response.manga.ChapterMangaResponse;
import com.jagt1806.mangareader.http.response.manga.FeedMangaResponse;
import com.jagt1806.mangareader.http.response.manga.MangaListResponse;
import com.jagt1806.mangareader.http.response.manga.MangaResponse;
import com.jagt1806.mangareader.service.MangaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MangaServiceImp implements MangaService {
    private final MangaClient mangaClient;

    private static final String INCLUDES = "cover_art";
    private static final List<String> SAFE_CONTENT = List.of("safe", "suggestive");
    private static final List<String> NSFW_CONTENT = List.of("erotica", "pornographic");

    @Override
    public MangaListResponse getMangas(String title, int offset, int limit, boolean nsfw, String language) {
        List<String> contentRatingFilters = new ArrayList<>(SAFE_CONTENT);
        if(nsfw) contentRatingFilters.addAll(NSFW_CONTENT);
        List<String> availableTranslatedLanguage = getLanguage(language);

        MangaList apiResponse = mangaClient.getSearchManga(title, INCLUDES, offset, limit, contentRatingFilters, availableTranslatedLanguage);

        return new MangaListResponse(
                apiResponse.getData().stream().map(data -> convertMangaDTO(data, language)).toList(),
                offset, limit, apiResponse.getTotal()
        );
    }

    @Override
    public MangaResponse getMangaId(String id, String language) {
        return new MangaResponse(
                convertMangaDTO(mangaClient.getMangaId(id, INCLUDES).getData(), language)
        );
    }

    @Override
    public FeedMangaResponse getFeed(String id, int offset, int limit, boolean nsfw, String language) {
        List<String> contentRatingFilters = new ArrayList<>(SAFE_CONTENT);
        if(nsfw) contentRatingFilters.addAll(NSFW_CONTENT);
        List<String> availableTranslatedLanguage = getLanguage(language);

        FeedList apiResponse = mangaClient.getMangaIdFeed(id, offset, limit, contentRatingFilters, null, "asc", "asc", availableTranslatedLanguage);

        return new FeedMangaResponse(
                apiResponse.getData().stream().map(this::convertFeedMangaDTO).toList(),
                offset, limit, apiResponse.getTotal()
        );
    }

    @Override
    public ChapterMangaResponse getChapter(String id) {
        Chapter apiResponse = mangaClient.getAtHomeServerChapterId(id);
        String baseUrl = apiResponse.getBaseUrl();
        ChapterAttributes attributes = apiResponse.getChapter();
        String hash = attributes.getHash();

        return  new ChapterMangaResponse(
                buildUrl(baseUrl, hash, attributes.getData(), "data"),
                buildUrl(baseUrl, hash, attributes.getData(), "data-saver")
        );
    }

    private List<String> getLanguage(String language) {
        return language.equals("es") ? List.of("es", "es-la") : List.of(language);
    }

    private MangaDTO convertMangaDTO(MangaData mangaData, String language) {
        MangaAttributes attributes = mangaData.getAttributes();
        String description = getDescription(attributes.getDescription(), language);
        String[] coverData = getCoverData(mangaData.getRelationships());

        return new MangaDTO(
                mangaData.getId(),
                attributes.getTitle().get("en"),
                description,
                coverData[0],
                coverData[1]
        );
    }

    private String getDescription(Map<String, String> description, String language) {
        if(description == null) return null;
        if(description.containsKey(language)) return description.get(language);
        if(language.equals("es") && description.containsKey("es-la")) return description.get("es-la");
        return null;
    }

    private String[] getCoverData(List<MangaRelationship> relationships) {
        return relationships.stream()
                .filter(r -> INCLUDES.equals(r.getType()))
                .map(r -> new String[]{r.getId(), Optional.ofNullable(r.getAttributes()).map(CoverAttributes::getFileName).orElse(null)})
                .findFirst()
                .orElse(new String[]{null, null});
    }

    private FeedMangaDTO convertFeedMangaDTO(Feed feed) {
        FeedAttributes attributes = feed.getAttributes();

        return new FeedMangaDTO(
                feed.getId(),
                new AuxFeedManga(
                        attributes.getVolume(), attributes.getChapter(), attributes.getTitle(), attributes.getTranslatedLanguage(), attributes.getPages()
                )
        );
    }

    private List<String> buildUrl(String baseUrl, String hash, List<String> data, String type) {
        return data.stream().map(img -> String.format("%s/%s/%s/%s", baseUrl, type, hash, img)).toList();
    }
}