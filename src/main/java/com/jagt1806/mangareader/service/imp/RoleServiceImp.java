package com.jagt1806.mangareader.service.imp;

import com.jagt1806.mangareader.exceptions.RoleNotFoundException;
import com.jagt1806.mangareader.exceptions.UniqueException;
import com.jagt1806.mangareader.model.Roles;
import com.jagt1806.mangareader.repository.RolesRepository;
import com.jagt1806.mangareader.http.request.role.RoleRequest;
import com.jagt1806.mangareader.http.response.role.RoleListResponse;
import com.jagt1806.mangareader.service.RoleService;
import com.jagt1806.mangareader.util.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {
    private final RolesRepository rolesRepository;
    private final MessageUtil messageUtil;

    @Override
    public RoleListResponse getAllRoles(String role, int offset, int limit) {
        List<Roles> roles;
        Pageable pageable = PageRequest.of(offset, limit);
        long total;

        if(role != null) {
            roles = rolesRepository.findByRolContaining(role.toUpperCase(), pageable).orElse(null);
            total = rolesRepository.countByRolContaining(role.toUpperCase());
        } else {
            roles = rolesRepository.findAll(pageable).getContent();
            total = rolesRepository.count();
        }

        RoleListResponse response = new RoleListResponse();
        response.setData(roles);
        response.setOffset(offset);
        response.setLimit(limit);
        response.setTotal(total);

        return response;
    }


    @Override
    public Roles getById(Long id) {
        return rolesRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(null));
    }

    @Override
    public void createRole(RoleRequest roleDTO) {
        if (roleDTO == null || roleDTO.getRole() == null || roleDTO.getRole().isEmpty()) {
            throw new IllegalArgumentException(messageUtil.getMessage("role.null"));
        }

        Roles role = new Roles();
        role.setRol(roleDTO.getRole().toUpperCase());

        try {
            rolesRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("roles_rol_key")) {
                throw new UniqueException(messageUtil.getMessage("role.unique"));
            }
            throw e;
        }
    }

    @Override
    public void updateRole(Long id, RoleRequest updatedRole) {
        if (id == null || updatedRole == null) {
            throw new IllegalArgumentException(messageUtil.getMessage("error.invalid.args"));
        }
        if (!rolesRepository.existsById(id)) {
            throw new RoleNotFoundException(null);
        }

        if (updatedRole.getRole() == null || updatedRole.getRole().isEmpty()) {
            throw new IllegalArgumentException(messageUtil.getMessage("role.null"));
        }

        Roles role = rolesRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(null));
        role.setRol(updatedRole.getRole().toUpperCase());

        try {
            rolesRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("roles_rol_key")) {
                throw new UniqueException(messageUtil.getMessage("role.unique"));
            }
            throw e;
        }

    }

    @Transactional
    @Override
    public void deleteRole(Long id) {
        rolesRepository.deleteUserRolesByRoleId(id);
        rolesRepository.deleteById(id);
    }

}