package com.proyecto.mangareader.app.responses.img;

import com.proyecto.mangareader.app.entity.ImgEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListImgResponse {
    @Schema(example = "OK")
    private final String message = "OK";
    private List<ImgEntity> data;
    private int offset;
    private int limit;
    private Long total;
}
