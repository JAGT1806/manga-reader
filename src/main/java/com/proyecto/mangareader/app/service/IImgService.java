package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.request.img.ImgRequest;
import com.proyecto.mangareader.app.responses.img.ListImgResponse;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de imágenes.
 * Define las operaciones básicas de CRUD para entidades de imagen.
 * @author Jhon Alexander Gómez Trujillo
 * @since 2004
 */
public interface IImgService {
    /**
     * Añade una nueva imagen al sistema.
     *
     * @param img Solicitud de creación de imagen con los detalles de la imagen a añadir
     * @throws IllegalArgumentException Si los datos de la imagen son inválidos
     */
    void addImg(ImgRequest img);
    /**
     * Recupera una lista paginada de todas las imágenes.
     *
     * @param offset Número de página para la paginación (comenzando desde 0)
     * @param limit Número máximo de elementos a recuperar por página
     * @return Respuesta que contiene la lista de imágenes
     * @throws IllegalArgumentException Si los parámetros de paginación son inválidos
     */
    ListImgResponse getAllImg(int offset, int limit);
    /**
     * Recupera una imagen específica por su identificador.
     *
     * @param id Identificador único de la imagen
     * @return Entidad de imagen correspondiente al ID proporcionado
     * @throws com.proyecto.mangareader.app.exceptions.ImgNotFoundException Si no se encuentra una imagen con el ID especificado
     */
    ImgEntity getImg(Long id);
    /**
     * Elimina una imagen específica del sistema.
     *
     * @param id Identificador de la imagen a eliminar
     * @throws com.proyecto.mangareader.app.exceptions.ImgNotFoundException Si no se encuentra una imagen con el ID especificado
     * @throws org.springframework.dao.DataIntegrityViolationException Si la imagen no puede ser eliminada debido a restricciones de integridad
     */
    void deleteImg(Long id);
    /**
     * Actualiza los detalles de una imagen existente.
     *
     * @param id Identificador de la imagen a actualizar
     * @param img Solicitud con los nuevos detalles de la imagen
     * @throws com.proyecto.mangareader.app.exceptions.ImgNotFoundException Si no se encuentra una imagen con el ID especificado
     * @throws IllegalArgumentException Si los datos de actualización son inválidos
     */
    void setImg(Long id, ImgRequest img);
}
