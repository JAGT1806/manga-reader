package com.jagt1806.mangareader.dto.api.attributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaRelationship {
    private String id;
    private String type;
    private CoverAttributes attributes;
}
