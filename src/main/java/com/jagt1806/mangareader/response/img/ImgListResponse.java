package com.jagt1806.mangareader.response.img;

import com.jagt1806.mangareader.model.Img;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImgListResponse {
    @Schema(example = "OK")
    private final String message = "OK";
    private List<Img> data;
    private int offset;
    private int limit;
    private Long total;
}
