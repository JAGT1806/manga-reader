package com.proyecto.mangareader.app.dto.out;

import lombok.Data;

import java.util.Map;

@Data
public class OutMangaDTO {
    private String id;
    private String title;
    private Map<String, Object> description;
    private String coverId;
    private String fileName;
}