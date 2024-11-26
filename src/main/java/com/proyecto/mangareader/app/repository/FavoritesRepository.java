package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.FavoritesEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de gestión de favoritos que extiende JpaRepository.
 * Proporciona métodos de acceso a datos para la entidad de favoritos.
 *
 * Métodos personalizados para operaciones específicas de favoritos.
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Repository
@Transactional
public interface FavoritesRepository extends JpaRepository<FavoritesEntity, Long> {
    /**
     * Recupera todos los favoritos de un usuario específico con paginación.
     *
     * @param userId Identificador del usuario
     * @param pageable Información de paginación (offset y límite)
     * @return Optional con la lista de entidades de favoritos del usuario
     * @throws com.proyecto.mangareader.app.exceptions.FavoriteNotFoundException Si no se encuentran favoritos para el usuario
     */
    Optional<List<FavoritesEntity>> findAllByUserId_IdUser(Long userId, Pageable pageable);

    /**
     * Elimina un favorito específico de un usuario por ID de usuario e ID de manga.
     *
     * @param userId Identificador del usuario
     * @param manga Identificador del manga a eliminar de favoritos
     */
    void deleteByUserId_IdUserAndIdManga(Long userId, String manga);

    /**
     * Verifica la existencia de un favorito para un usuario y manga específicos.
     *
     * @param userId Identificador del usuario
     * @param idManga Identificador del manga
     * @return true si el favorito existe, false en caso contrario
     */
    boolean existsByUserIdIdUserAndIdManga(Long userId, String idManga);

    /**
     * Cuenta el número total de favoritos para un usuario específico.
     *
     * @param userId Identificador del usuario
     * @return Número total de favoritos del usuario
     */
    Long countFavoritesByUserId_IdUser(Long userId);

    /**
     * Elimina todos los favoritos de un usuario específico.
     *
     * @param userId Identificador del usuario
     */
    void deleteAllByUserId_IdUser(Long userId);
}
