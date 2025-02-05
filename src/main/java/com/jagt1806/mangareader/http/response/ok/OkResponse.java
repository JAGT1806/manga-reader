package com.jagt1806.mangareader.http.response.ok;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OkResponse {
    @Schema(example = "Ok")
    private final String message = "Ok";
}
