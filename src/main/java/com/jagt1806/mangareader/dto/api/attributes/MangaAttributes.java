package com.jagt1806.mangareader.dto.api.attributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MangaAttributes {
    private Map<String, String> title;
    private Map<String, String> description;
}
