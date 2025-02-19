package com.jagt1806.mangareader.http.response.manga;

import com.jagt1806.mangareader.dto.api.Feed;
import lombok.Data;

import java.util.List;

@Data
public class FeedListResponse {
    private List<Feed> data;
    private int offset;
    private int limit;
    private long total;
}
