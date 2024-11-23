package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.request.roles.RolesRequest;
import com.proyecto.mangareader.app.entity.RolesEntity;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.role.RoleListResponse;

public interface IRolesService {
    public RoleListResponse getAllRoles(String role);

    public RolesEntity createRole(RolesRequest role);

    public OkResponse deleteRole(Long id);

    public RolesEntity updateRole(Long id, RolesRequest updatedRole);

    public RolesEntity getById(Long id);

}
