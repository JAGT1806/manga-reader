package com.proyecto.mangareader.app.controller;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.request.img.ImgRequest;
import com.proyecto.mangareader.app.responses.error.ErrorResponse;
import com.proyecto.mangareader.app.responses.img.ListImgResponse;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.service.IImgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de imágenes.
 * Proporciona endpoints para realizar operaciones CRUD sobre recursos de imágenes.
 */
@RestController
@RequestMapping("/api/img")
@RequiredArgsConstructor
@Tag(name="Imgs", description = "Gestión de imágenes")
public class ImgController {
    /** Servicio para la gestión de operaciones de imágenes. */
    private final IImgService imgService;

    /**
     * Recupera una lista paginada de imágenes.
     *
     * @param offset Número de página para la paginación (por defecto 0)
     * @param limit Número de elementos por página (por defecto 10)
     * @return Respuesta con la lista de imágenes
     */
    @Operation(summary = "Listar imágenes", description = "Recupera una lista paginada de imágenes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de imágenes recuperada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListImgResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ListImgResponse> getImgs(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        ListImgResponse response = imgService.getAllImg(offset, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Recupera una imagen específica por su identificador.
     *
     * @param id Identificador único de la imagen
     * @return Entidad de imagen solicitada
     */
    @Operation(summary = "Obtener imagen", description = "Recupera una imagen específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImgEntity.class))),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ImgEntity getImg(@PathVariable Long id) {
        return imgService.getImg(id);
    }

    /**
     * Añade una nueva imagen al sistema.
     *
     * @param request Detalles de la imagen a crear
     * @return Respuesta de operación exitosa
     */
    @Operation(summary = "Crear imagen", description = "Añade una nueva imagen al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de imagen inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<OkResponse> addImg(@RequestBody ImgRequest request) {
        imgService.addImg(request);
        return ResponseEntity.ok(new OkResponse());
    }

    /**
     * Actualiza una imagen existente.
     *
     * @param id Identificador de la imagen a actualizar
     * @param request Nuevos detalles de la imagen
     * @return Respuesta de operación exitosa
     */
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OkResponse> updateImg(@PathVariable Long id, @RequestBody ImgRequest request) {
        imgService.setImg(id, request);
        return ResponseEntity.ok(new OkResponse());
    }

    /**
     * Elimina una imagen específica.
     *
     * @param id Identificador de la imagen a eliminar
     * @return Respuesta de operación exitosa
     */
    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen eliminada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OkResponse.class))),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> deleteImg(@PathVariable Long id) {
        imgService.deleteImg(id);
        return ResponseEntity.ok(new OkResponse());
    }

}
