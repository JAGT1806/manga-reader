package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.favorites.FavoriteDTO;
import com.proyecto.mangareader.app.entity.FavoritesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.exceptions.FavoriteNotFoundException;
import com.proyecto.mangareader.app.exceptions.UniqueException;
import com.proyecto.mangareader.app.exceptions.UserNotFoundException;
import com.proyecto.mangareader.app.repository.FavoritesRepository;
import com.proyecto.mangareader.app.repository.UsersRepository;
import com.proyecto.mangareader.app.request.favorites.FavoritesRequest;
import com.proyecto.mangareader.app.responses.favorites.ListFavoritesResponse;
import com.proyecto.mangareader.app.service.IFavoritesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de servicios de favoritos.
 * Gestiona las operaciones de recuperación, creación y eliminación de favoritos.
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Service
@RequiredArgsConstructor
public class FavoritesService implements IFavoritesService {
    /** Repositorio para operaciones de favoritos */
    private final FavoritesRepository favoritesRepository;
    /** Repositorio para operaciones de usuarios */
    private final UsersRepository usersRepository;
    /** Utilidad para gestión de mensajes */
    private final MessageUtil messageUtil;

    /**
     * Recupera todos los favoritos con paginación.
     *
     * @param offset Número de página para la paginación
     * @param limit Número de elementos por página
     * @return Respuesta con la lista de favoritos
     */
    @Override
    public ListFavoritesResponse getAllFavorites(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<FavoritesEntity> favorites = favoritesRepository.findAll(pageable).getContent();

        ListFavoritesResponse response = new ListFavoritesResponse();

        List<FavoriteDTO> dto = favorites.stream().map(this::setFavoriteDTO).toList();

        long total = favoritesRepository.count();

        response.setData(dto);
        response.setTotal(total);
        response.setOffset(offset);
        response.setLimit(limit);

        return response;
    }

    /**
     * Convierte una entidad de favoritos a un DTO para transferencia de datos.
     *
     * @param favorites Entidad de favoritos
     * @return DTO de favorito
     */
    private FavoriteDTO setFavoriteDTO(FavoritesEntity favorites) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorites.getIdFavorites());
        dto.setUserId(favorites.getUserId().getIdUser());
        dto.setIdManga(favorites.getIdManga());
        dto.setNameManga(favorites.getNameManga());
        dto.setUrlImage(favorites.getUrlImage());

        return dto;
    }

    /**
     * Busca un favorito por su identificador único.
     *
     * @param id Identificador del favorito
     * @return Entidad de favorito encontrada
     * @throws com.proyecto.mangareader.app.exceptions.FavoriteNotFoundException Si no se encuentra el favorito
     */
    @Override
    public FavoritesEntity getFavoriteById(Long id) {
        return favoritesRepository.findById(id).orElseThrow(() -> new FavoriteNotFoundException(messageUtil.getMessage("favorite.not.found")));
    }

    /**
     * Recupera los favoritos de un usuario específico.
     *
     * @param userId Identificador del usuario
     * @param offset Número de página para la paginación
     * @param limit Número de elementos por página
     * @return Respuesta con la lista de favoritos del usuario
     * @throws com.proyecto.mangareader.app.exceptions.FavoriteNotFoundException Si no se encuentran favoritos para el usuario
     */
    @Override
    public ListFavoritesResponse getFavoritesByUserId(Long userId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<FavoritesEntity> favorites = favoritesRepository.findAllByUserId_IdUser(userId, pageable).orElseThrow(() -> new FavoriteNotFoundException(messageUtil.getMessage("favorites.user.not.found")));

        ListFavoritesResponse response = new ListFavoritesResponse();

        List<FavoriteDTO> dto = favorites.stream().map(this::setFavoriteDTO).toList();



        Long total = favoritesRepository.countFavoritesByUserId_IdUser(userId);

        response.setData(dto);
        response.setTotal(total);
        response.setOffset(offset);
        response.setLimit(limit);

        return response;
    }

    /**
     * Crea un nuevo favorito para un usuario.
     *
     * @param request Solicitud de creación de favorito
     * @return Entidad de favorito creada
     * @throws com.proyecto.mangareader.app.exceptions.UserNotFoundException Si el usuario no existe
     * @throws com.proyecto.mangareader.app.exceptions.UniqueException Si el favorito ya existe
     */
    @Override
    public FavoritesEntity createFavorite(FavoritesRequest request) {
        try {
            FavoritesEntity favorite = new FavoritesEntity();
            favorite.setIdManga(request.getIdManga());
            UsersEntity user = usersRepository.findById(request.getUserId()).orElseThrow( () -> new UserNotFoundException(messageUtil.getMessage("user.not.found")) );
            favorite.setUserId(user);
            favorite.setNameManga(request.getNameManga());
            favorite.setUrlImage(request.getUrlImage());

            return favoritesRepository.save(favorite);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("unique_user_manga")) {
                throw new UniqueException(messageUtil.getMessage("user.unique"));
            }
            throw e;

        }
    }

    /**
     * Elimina un favorito específico de un usuario.
     *
     * @param id Identificador del usuario
     * @param mangaId Identificador del manga a eliminar
     */
    @Override
    public void deleteFavoriteByIds(Long id, String mangaId) {
        favoritesRepository.deleteByUserId_IdUserAndIdManga(id, mangaId);
    }

    /**
     * Verifica si un manga ya existe en favoritos de un usuario.
     *
     * @param userId Identificador del usuario
     * @param mangaId Identificador del manga
     * @return Verdadero si el manga existe en favoritos, falso en caso contrario
     */
    @Override
    public Boolean existsFavorite(Long userId, String mangaId) {
        return favoritesRepository.existsByUserIdIdUserAndIdManga(userId, mangaId);
    }

}