package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.UsersEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la gestión de usuarios en la aplicación Manga Reader.
 *
 * Extiende JpaRepository para proporcionar operaciones CRUD estándar para entidades de usuario,
 * e incluye métodos personalizados de consulta para búsquedas avanzadas y verificaciones.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    /**
     * Busca usuarios utilizando filtros dinámicos flexibles.
     *
     * Permite realizar búsquedas combinando múltiples criterios opcionales:
     * - Nombre de usuario (parcial)
     * - Correo electrónico (parcial)
     * - Rol de usuario
     * - Estado de habilitación
     *
     * @param username Filtro opcional para nombre de usuario (búsqueda parcial)
     * @param email Filtro opcional para correo electrónico (búsqueda parcial)
     * @param role Filtro opcional para rol de usuario (búsqueda parcial)
     * @param enabled Filtro opcional para estado de habilitación del usuario
     * @param pageable Parámetros de paginación para la consulta
     * @return Lista de usuarios que coinciden con los criterios de búsqueda
     */
    @Query("SELECT DISTINCT u FROM UsersEntity u LEFT JOIN u.roles r WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR r.rol LIKE %:role%) AND " +
            "(:enabled IS NULL OR u.enabled = :enabled)")

    List<UsersEntity> findByFilters(@Param("username") String username,
                                    @Param("email") String email,
                                    @Param("role") String role,
                                    @Param("enabled") Boolean enabled,
                                    Pageable pageable);

    /**
     * Cuenta el número total de usuarios que coinciden con los filtros especificados.
     *
     * Método complementario a findByFilters para obtener el total de resultados sin paginar.
     * Útil para cálculos de paginación y recuento de resultados.
     *
     * @param username Filtro opcional para nombre de usuario (búsqueda parcial)
     * @param email Filtro opcional para correo electrónico (búsqueda parcial)
     * @param role Filtro opcional para rol de usuario (búsqueda parcial)
     * @param enabled Filtro opcional para estado de habilitación del usuario
     * @return Número total de usuarios que coinciden con los criterios de búsqueda
     */
    @Query("SELECT COUNT(DISTINCT u) FROM UsersEntity u LEFT JOIN u.roles r WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR r.rol LIKE %:role%) AND " +
            "(:enabled IS NULL OR u.enabled = :enabled)")
    Long countByFilters(@Param("username") String username,
                        @Param("email") String email,
                        @Param("role") String role,
                        @Param("enabled") Boolean enabled);


    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param email Correo electrónico del usuario a buscar
     * @return Optional que contiene el usuario si existe, vacío en caso contrario
     */
    Optional<UsersEntity> findByEmail(String email);

    /**
     * Verifica la existencia de un usuario con un correo electrónico específico.
     *
     * Útil para validaciones de registro y prevención de duplicados.
     *
     * @param email Correo electrónico a verificar
     * @return true si existe un usuario con ese correo, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Recupera todos los usuarios que no están habilitados.
     *
     * Método útil para procesos de administración, como gestión de usuarios pendientes
     * de verificación o activación.
     *
     * @return Optional que contiene la lista de usuarios no habilitados
     */
    Optional<List<UsersEntity>> findByEnabledFalse();
}
