package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.in.InRolesDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.exceptions.RoleNotFoundException;
import com.proyecto.mangareader.app.repository.RolesRepository;
import com.proyecto.mangareader.app.responses.OkResponse;
import com.proyecto.mangareader.app.service.IRolesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RolesService implements IRolesService {
    private final RolesRepository rolesRepository;

    @Override
    public List<RolesEntity> getAllRoles(String role) {
        if(role != null) {
            return rolesRepository.findByRolContaining(role).orElseThrow();
        }
        return rolesRepository.findAll();
    }

    @Override
    public RolesEntity getById(Long id) {
        if(id != null) {
            return rolesRepository.findById(id).orElse(null);
        } else {
            throw new IllegalArgumentException("No se encontró el rol con el ID proporcionado.");
        }
    }

    @Override
    public RolesEntity saveRole(RolesEntity role) {
        if (role == null || role.getRol() == null || role.getRol().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío.");
        }
        return rolesRepository.save(role);
    }

    @Override
    public OkResponse deleteRole(Long id) {
        RolesEntity role = rolesRepository.findById(id).orElse(null);
        if(role == null || role.getRol() == null || role.getRol().isEmpty()) {
            throw new RoleNotFoundException("Id no encontrado");
        }
        rolesRepository.deleteById(id);
        return new OkResponse();
    }

    @Override
    public RolesEntity updateRole(Long id, InRolesDTO updatedRole) {
        if (id == null || updatedRole == null) {
            throw new IllegalArgumentException("El ID del rol o los datos actualizados no pueden ser nulos.");
        }
        if (!rolesRepository.existsById(id)) {
            throw new RoleNotFoundException("El rol con ID " + id + " no existe.");
        }

        if (updatedRole.getRole() == null || updatedRole.getRole().isEmpty()) {
            throw new IllegalArgumentException("El campo de Rol no debe ser nulo.");
        }

        RolesEntity existingRole = rolesRepository.findById(id).orElse(null);
        if (existingRole != null) {
            existingRole.setRol(updatedRole.getRole()); // Actualiza solo los campos necesarios
            return rolesRepository.save(existingRole);
        } else {
            throw new IllegalArgumentException("No se encontró el rol con el ID proporcionado.");
        }
    }
}
