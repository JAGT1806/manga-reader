package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.entity.ImgEntity;
import com.proyecto.mangareader.app.exceptions.ImgNotFoundException;
import com.proyecto.mangareader.app.repository.ImgRepository;
import com.proyecto.mangareader.app.request.img.ImgRequest;
import com.proyecto.mangareader.app.responses.img.ListImgResponse;
import com.proyecto.mangareader.app.service.IImgService;
import com.proyecto.mangareader.app.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de gestión de imágenes para la aplicación Manga Reader.
 *
 * Proporciona funcionalidades para administrar entidades de imágenes,
 * incluyendo operaciones CRUD y recuperación paginada.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Service
@AllArgsConstructor
public class ImgService implements IImgService {
    /** Repositorio para operaciones de persistencia de imágenes. */
    private final ImgRepository imgRepository;
    /** Utilidad para gestión de mensajes localizados. */
    private final MessageUtil messageSource;

    /**
     * Añade una nueva imagen al sistema.
     *
     * Crea una nueva entidad de imagen con la URL proporcionada
     * y la persiste en la base de datos.
     *
     * @param request Solicitud de imagen con la URL a guardar
     */
    @Override
    public void addImg(ImgRequest request) {
        ImgEntity img = new ImgEntity();
        img.setUrl(request.getUrl());
        imgRepository.save(img);
    }

    /**
     * Recupera todas las imágenes de forma paginada.
     *
     * Obtiene una lista de imágenes con paginación y calcula el total de registros.
     *
     * @param offset Número de página a recuperar (base cero)
     * @param limit Número de elementos por página
     * @return Respuesta con lista de imágenes, metadatos de paginación y total
     */
    @Override
    public ListImgResponse getAllImg(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);

        List<ImgEntity> imgs = imgRepository.findAll(pageable).getContent();
        Long total = imgRepository.count();

        return convertToDto(imgs, offset, limit, total);
    }

    /**
     * Actualiza la URL de una imagen existente.
     *
     * Modifica la imagen identificada por el ID proporcionado.
     * Lanza una excepción si la URL es nula.
     *
     * @param id Identificador de la imagen a modificar
     * @param request Solicitud con la nueva URL de imagen
     * @throws IllegalArgumentException Si la URL es nula
     * @throws ImgNotFoundException Si no se encuentra la imagen
     */
    @Override
    public void setImg(Long id, ImgRequest request) {
        if(request.getUrl() == null) {
            throw new IllegalArgumentException(messageSource.getMessage("img.null"));
        }

        ImgEntity img = imgRepository.findById(id).orElseThrow(() -> new ImgNotFoundException(null));
        img.setUrl(request.getUrl());

        imgRepository.save(img);
    }

    /**
     * Recupera una imagen específica por su identificador.
     *
     * @param id Identificador de la imagen a recuperar
     * @return Entidad de imagen correspondiente al ID
     * @throws ImgNotFoundException Si no se encuentra la imagen
     */
    @Override
    public ImgEntity getImg(Long id) {
        return imgRepository.findById(id).orElseThrow(() -> new ImgNotFoundException(null));
    }

    /**
     * Elimina una imagen del sistema por su identificador.
     *
     * @param id Identificador de la imagen a eliminar
     */
    @Override
    public void deleteImg(Long id) {
        imgRepository.deleteById(id);
    }


    /**
     * Convierte una lista de entidades de imagen a un objeto de respuesta DTO.
     *
     * Método auxiliar para transformar los resultados de repositorio
     * en un objeto de respuesta con metadatos de paginación.
     *
     * @param imgs Lista de entidades de imagen
     * @param offset Número de página actual
     * @param limit Tamaño de página
     * @param total Número total de imágenes
     * @return Objeto de respuesta con datos de imágenes y metadatos
     */
    private ListImgResponse convertToDto(List<ImgEntity> imgs, int offset, int limit, Long total) {
        ListImgResponse response = new ListImgResponse();
        response.setData(imgs);
        response.setOffset(offset);
        response.setLimit(limit);
        response.setTotal(total);

        return response;
    }
}
