package com.jagt1806.mangareader.dto.api;

import lombok.Data;

import java.util.List;

@Data
public class FeedList {
    private List<Feed> data;
    private int offset;
    private int limit;
    private long total;
}
