package com.jagt1806.mangareader.controller;

import com.jagt1806.mangareader.model.Img;
import com.jagt1806.mangareader.http.request.img.ImgRequest;
import com.jagt1806.mangareader.http.response.error.ErrorResponse;
import com.jagt1806.mangareader.http.response.img.ImgListResponse;
import com.jagt1806.mangareader.http.response.ok.OkResponse;
import com.jagt1806.mangareader.service.ImgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/img")
@RequiredArgsConstructor
@Tag(name="Images", description = "Gestión de imágenes")
public class ImgController {
    private final ImgService imgService;

    @Operation(summary = "Listar imágenes", description = "Recupera una lista paginada de imágenes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de imágenes recuperada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImgListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ImgListResponse> getAllImg (
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        ImgListResponse response = imgService.getAllImg(offset, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener imagen", description = "Recupera una imagen específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Img.class))),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Img> getImg(@PathVariable Long id) {
        return ResponseEntity.ok(imgService.getById(id));
    }

    @Operation(summary = "Crear imagen", description = "Añade una nueva imagen al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de imagen inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<OkResponse> addImg(@RequestBody ImgRequest request) {
        imgService.createImg(request);
        return ResponseEntity.ok(new OkResponse());
    }

    @Operation(summary = "Actualizar imagen", description = "Actualiza una imagen existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<OkResponse> updateImg(@PathVariable Long id, @RequestBody ImgRequest request) {
        imgService.updateImg(id, request);
        return ResponseEntity.ok(new OkResponse());
    }

    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen eliminada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> deleteImg(@PathVariable Long id) {
        imgService.deleteImg(id);
        return ResponseEntity.ok(new OkResponse());
    }
}
