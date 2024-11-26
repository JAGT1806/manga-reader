package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.FavoritesEntity;
import com.proyecto.mangareader.app.request.favorites.FavoritesRequest;
import com.proyecto.mangareader.app.responses.favorites.ListFavoritesResponse;

/**
 * Interfaz que define las operaciones de gestión de favoritos.
 * Proporciona métodos para recuperar, crear y eliminar favoritos.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
public interface IFavoritesService {
    /**
     * Recupera todos los favoritos con paginación.
     *
     * @param offset Número de página para la paginación
     * @param limit Número de elementos por página
     * @return Respuesta con la lista de favoritos
     */
    ListFavoritesResponse getAllFavorites(int offset, int limit);

    /**
     * Busca un favorito por su identificador único.
     *
     * @param id Identificador del favorito
     * @return Entidad de favorito encontrada
     * @throws com.proyecto.mangareader.app.exceptions.FavoriteNotFoundException Si no se encuentra el favorito
     */
    FavoritesEntity getFavoriteById(Long id);

    /**
     * Recupera los favoritos de un usuario específico.
     *
     * @param userId Identificador del usuario
     * @param offset Número de página para la paginación
     * @param limit Número de elementos por página
     * @return Respuesta con la lista de favoritos del usuario
     * @throws com.proyecto.mangareader.app.exceptions.FavoriteNotFoundException Si no se encuentran favoritos para el usuario
     */
    ListFavoritesResponse getFavoritesByUserId(Long userId, int offset, int limit);

    /**
     * Elimina un favorito específico de un usuario.
     *
     * @param id Identificador del usuario
     * @param mangaId Identificador del manga a eliminar
     */
    void deleteFavoriteByIds(Long id, String mangaId);

    /**
     * Crea un nuevo favorito para un usuario.
     *
     * @param favorite Solicitud de creación de favorito
     * @return Entidad de favorito creada
     * @throws com.proyecto.mangareader.app.exceptions.UserNotFoundException Si el usuario no existe
     * @throws com.proyecto.mangareader.app.exceptions.UniqueException Si el favorito ya existe
     */
    FavoritesEntity createFavorite(FavoritesRequest favorite);

    /**
     * Verifica si un manga ya existe en favoritos de un usuario.
     *
     * @param userid Identificador del usuario
     * @param mangaId Identificador del manga
     * @return Verdadero si el manga existe en favoritos, falso en caso contrario
     */
    Boolean existsFavorite(Long userid, String mangaId);
}
