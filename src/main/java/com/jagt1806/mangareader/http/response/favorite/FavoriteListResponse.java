package com.jagt1806.mangareader.http.response.favorite;

import com.jagt1806.mangareader.dto.favorite.FavoriteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteListResponse {
    private List<FavoriteDTO> data;
    private int offset;
    private int limit;
    private long total;
}
