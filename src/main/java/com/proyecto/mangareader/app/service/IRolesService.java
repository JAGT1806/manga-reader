package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.dto.in.RolesDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;

import java.util.List;

public interface IRolesService {
    public List<RolesEntity> getAllRoles(String role);

    public RolesEntity saveRole(RolesEntity role);

    public String deleteRole(Long id);

    public RolesEntity updateRole(Long id, RolesDTO updatedRole);

    public RolesEntity getById(Long id);
}
