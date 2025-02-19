package com.jagt1806.mangareader.dto.api.attributes;

import lombok.Data;

import java.util.List;

@Data
public class ChapterAttributes {
    private String hash;
    private List<String> data;
    private List<String> dataSaver;
}
