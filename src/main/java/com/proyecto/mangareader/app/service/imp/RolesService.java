package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.request.roles.RolesRequest;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.exceptions.RoleNotFoundException;
import com.proyecto.mangareader.app.exceptions.UniqueException;
import com.proyecto.mangareader.app.repository.RolesRepository;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.role.RoleListResponse;
import com.proyecto.mangareader.app.service.IRolesService;
import com.proyecto.mangareader.app.util.MessageUtil;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de servicios para la gestión de roles.
 *
 * Esta clase proporciona la lógica de negocio para las operaciones
 * de roles, utilizando un repositorio para la persistencia de datos.
 *
 * @author Jhon Alexander Gómez Trujillo
 * @since 2024
 */
@Service
@AllArgsConstructor
public class RolesService implements IRolesService {
    /** Repositorio para operaciones de persistencia de roles */
    private final RolesRepository rolesRepository;

    /** Utilidad para gestión de mensajes de sistema */
    private final MessageUtil messageUtil;

    /**
     * Recupera todos los roles, con posibilidad de filtrado por nombre.
     *
     * Si se proporciona un nombre de rol, realiza una búsqueda con coincidencia parcial.
     * Si no se proporciona, devuelve todos los roles del sistema.
     *
     * @param role Nombre del rol para filtrar (opcional)
     * @return Respuesta con la lista de roles encontrados
     */
    @Override
    public RoleListResponse getAllRoles(String role) {
        List<RolesEntity> roles;
        if(role != null) {
            roles = rolesRepository.findByRolContaining(role).orElse(null);
        } else {
            roles = rolesRepository.findAll();
        }

        RoleListResponse response = new RoleListResponse();
        response.setData(roles);
        return response;
    }

    /**
     * Recupera un rol específico por su identificador.
     *
     * @param id Identificador único del rol
     * @return La entidad de rol correspondiente
     * @throws RoleNotFoundException Si no se encuentra un rol con el ID proporcionado
     */
    @Override
    public RolesEntity getById(Long id) {
            return rolesRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(null));
    }

    /**
     * Crea un nuevo rol en el sistema.
     *
     * Valida que el rol tenga un nombre y lo guarda en mayúsculas.
     * Maneja excepciones de integridad de datos para prevenir roles duplicados.
     *
     * @param roleDTO Datos del nuevo rol a crear
     * @return El rol recién creado
     * @throws IllegalArgumentException Si los datos del rol son inválidos
     * @throws UniqueException Si ya existe un rol con el mismo nombre
     */
    @Override
    public RolesEntity createRole(RolesRequest roleDTO) {
        if (roleDTO == null || roleDTO.getRole() == null || roleDTO.getRole().isEmpty()) {
            throw new IllegalArgumentException(messageUtil.getMessage("role.null"));
        }

        RolesEntity role = new RolesEntity();
        role.setRol(roleDTO.getRole().toUpperCase());

        try {
            return rolesRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("ukg00thobnv3twutok8x6furkx1")) {
                throw new UniqueException(messageUtil.getMessage("role.unique"));
            }
            throw e;
        }
    }

    /**
     * Elimina un rol por su identificador.
     *
     * Realiza la eliminación directa del rol en la base de datos.
     *
     * @param id Identificador del rol a eliminar
     * @return Respuesta de confirmación de eliminación
     */
    @Override
    public OkResponse deleteRole(Long id) {
        rolesRepository.deleteById(id);
        return new OkResponse();
    }

    /**
     * Actualiza la información de un rol existente.
     *
     * Valida la existencia del rol, verifica que los datos sean válidos
     * y actualiza el nombre del rol en mayúsculas.
     *
     * @param id Identificador del rol a actualizar
     * @param updatedRole Nuevos datos del rol
     * @return El rol actualizado
     * @throws IllegalArgumentException Si los datos son inválidos
     * @throws RoleNotFoundException Si no se encuentra el rol
     */
    @Override
    public RolesEntity updateRole(Long id, RolesRequest updatedRole) {
        if (id == null || updatedRole == null) {
            throw new IllegalArgumentException(messageUtil.getMessage("error.invalid.args"));
        }
        if (!rolesRepository.existsById(id)) {
            throw new RoleNotFoundException(null);
        }

        if (updatedRole.getRole() == null || updatedRole.getRole().isEmpty()) {
            throw new IllegalArgumentException(messageUtil.getMessage("role.null"));
        }

        RolesEntity existingRole = rolesRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(null));
        existingRole.setRol(updatedRole.getRole());

        return rolesRepository.save(existingRole);

    }
}
