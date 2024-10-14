package com.proyecto.mangareader.app.service.imp;

import com.proyecto.mangareader.app.dto.in.RolesDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.repository.RolesRepository;
import com.proyecto.mangareader.app.service.IRolesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RolesService implements IRolesService {
    private final RolesRepository rolesRepository;

    @Override
    public List<RolesEntity> getAllRoles(String role) {
        if(role != null) {
            return rolesRepository.findByRolContaining(role).orElse(null);
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
    public String deleteRole(Long id) {
        if(id != null || rolesRepository.existsById(id)) {
            rolesRepository.deleteById(id);
            return "Role deleted";
        } else {
            return "Role with " + id + " not found";
        }
    }

    @Override
    public RolesEntity updateRole(Long id, RolesDTO updatedRole) {
        if (id == null || updatedRole == null) {
            throw new IllegalArgumentException("El ID del rol o los datos actualizados no pueden ser nulos.");
        }
        if (!rolesRepository.existsById(id)) {
            throw new IllegalArgumentException("El rol con ID " + id + " no existe.");
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
