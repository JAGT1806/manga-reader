package com.jagt1806.mangareader.dto.api;

import com.jagt1806.mangareader.dto.api.attributes.FeedAttributes;
import lombok.Data;

@Data
public class Feed {
    private String id;
    private FeedAttributes attributes;
}
