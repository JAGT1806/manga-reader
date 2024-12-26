package com.jagt1806.mangareader.service;

import com.jagt1806.mangareader.model.Roles;
import com.jagt1806.mangareader.request.role.RoleRequest;
import com.jagt1806.mangareader.response.role.RoleListResponse;

public interface RoleService {
    RoleListResponse getAllRoles(String role, int offset, int limit);

    Roles getById(Long id);

    void createRole(RoleRequest request);

    void deleteRole(Long id);

    void updateRole(Long id, RoleRequest request);
}
