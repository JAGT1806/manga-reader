package com.jagt1806.mangareader.dto.manga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MangaDTO {
    private String id;
    private String title;
    private String description;
    private String coverId;
    private String fileName;
}