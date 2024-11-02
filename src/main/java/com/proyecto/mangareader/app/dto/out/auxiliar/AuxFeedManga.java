package com.proyecto.mangareader.app.dto.out.auxiliar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuxFeedManga {
    private String volumen;
    private String capitulo;
    private String titulo;
    private String idioma;
    private int paginas;
}
