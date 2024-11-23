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

@Service
@AllArgsConstructor
public class RolesService implements IRolesService {
    private final RolesRepository rolesRepository;
    private final MessageUtil messageSource;

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


    @Override
    public RolesEntity getById(Long id) {
            return rolesRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(null));
    }

    @Override
    public RolesEntity createRole(RolesRequest roleDTO) {
        if (roleDTO == null || roleDTO.getRole() == null || roleDTO.getRole().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("role.null"));
        }

        RolesEntity role = new RolesEntity();
        role.setRol(roleDTO.getRole().toUpperCase());

        try {
            return rolesRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("ukg00thobnv3twutok8x6furkx1")) {
                throw new UniqueException(messageSource.getMessage("role.unique"));
            }
            throw e;
        }
    }

    @Override
    public OkResponse deleteRole(Long id) {
        rolesRepository.deleteById(id);
        return new OkResponse();
    }

    @Override
    public RolesEntity updateRole(Long id, RolesRequest updatedRole) {
        if (id == null || updatedRole == null) {
            throw new IllegalArgumentException(messageSource.getMessage("error.invalid.args"));
        }
        if (!rolesRepository.existsById(id)) {
            throw new RoleNotFoundException(null);
        }

        if (updatedRole.getRole() == null || updatedRole.getRole().isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("role.null"));
        }

        RolesEntity existingRole = rolesRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(null));
        existingRole.setRol(updatedRole.getRole());

        return rolesRepository.save(existingRole);

    }
}
