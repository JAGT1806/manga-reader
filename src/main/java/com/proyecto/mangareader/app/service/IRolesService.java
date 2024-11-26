package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.request.roles.RolesRequest;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.role.RoleListResponse;

/**
 * Interfaz que define los servicios para la gestión de roles en el sistema.
 *
 * Proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para entidades de roles.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
public interface IRolesService {
    /**
     * Recupera todos los roles del sistema, con opción de filtrar por nombre.
     *
     * @param role Cadena opcional para filtrar roles por su nombre.
     *             Si es nulo, se devuelven todos los roles.
     * @return Respuesta que contiene la lista de roles
     */
    RoleListResponse getAllRoles(String role);

    /**
     * Crea un nuevo rol en el sistema.
     *
     * @param role Objeto de solicitud con la información del nuevo rol
     * @return La entidad de rol recién creada
     * @throws IllegalArgumentException Si los datos del rol son inválidos
     * @throws com.proyecto.mangareader.app.exceptions.UniqueException Si ya existe un rol con el mismo nombre
     */
    RolesEntity createRole(RolesRequest role);

    /**
     * Elimina un rol por su identificador.
     *
     * @param id Identificador único del rol a eliminar
     * @return Respuesta de confirmación de eliminación
     */
    OkResponse deleteRole(Long id);

    /**
     * Actualiza la información de un rol existente.
     *
     * @param id Identificador del rol a actualizar
     * @param updatedRole Objeto con los nuevos datos del rol
     * @return La entidad de rol actualizada
     * @throws IllegalArgumentException Si los datos son inválidos
     * @throws com.proyecto.mangareader.app.exceptions.RoleNotFoundException Si no se encuentra el rol
     */
    RolesEntity updateRole(Long id, RolesRequest updatedRole);

    /**
     * Busca un rol por su identificador único.
     *
     * @param id Identificador del rol a buscar
     * @return La entidad de rol encontrada
     * @throws com.proyecto.mangareader.app.exceptions.RoleNotFoundException Si no se encuentra el rol
     */
    RolesEntity getById(Long id);
}
