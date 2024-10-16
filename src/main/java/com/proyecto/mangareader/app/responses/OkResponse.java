package com.proyecto.mangareader.app.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OkResponse {
    @Schema(example = "Ok")
    private final String result = "Ok";
}
