package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la gestión de roles de usuario en la aplicación Manga Reader.
 *
 * Extiende JpaRepository para proporcionar operaciones CRUD estándar para entidades de roles,
 * e incluye métodos personalizados de consulta para búsquedas de roles.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Repository
public interface RolesRepository extends JpaRepository<RolesEntity, Long> {
    /**
     * Busca un rol específico por su nombre exacto.
     *
     * Permite recuperar un rol utilizando su identificador textual completo.
     * Útil para operaciones que requieren un rol específico, como asignación de permisos.
     *
     * @param role Nombre completo del rol a buscar (ej. "ROLE_USER", "ROLE_ADMIN")
     * @return Optional que contiene el rol si existe, vacío en caso contrario
     */
    Optional<RolesEntity> findByRol(String role);

    /**
     * Busca roles que contengan una cadena específica en su nombre.
     *
     * Permite realizar búsquedas parciales de roles, facilitando la recuperación
     * de grupos de roles relacionados o la implementación de filtros flexibles.
     *
     * @param role Cadena parcial a buscar en el nombre de los roles
     * @return Optional que contiene una lista de roles que coinciden con el criterio
     */
    Optional<List<RolesEntity>> findByRolContaining(String role);
}
