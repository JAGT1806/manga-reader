package com.jagt1806.mangareader.dto.api;

import com.jagt1806.mangareader.dto.api.attributes.MangaAttributes;
import com.jagt1806.mangareader.dto.api.attributes.MangaRelationship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaData {
    private String id;
    private MangaAttributes attributes;
    private List<MangaRelationship> relationships;
}
