package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.dto.in.InRolesDTO;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.role.RoleListResponse;

import java.util.List;

public interface IRolesService {
    public RoleListResponse getAllRoles(String role);

    public RolesEntity saveRole(RolesEntity role);

    public OkResponse deleteRole(Long id);

    public RolesEntity updateRole(Long id, InRolesDTO updatedRole);

    public RolesEntity getById(Long id);

}
