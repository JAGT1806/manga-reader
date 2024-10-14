package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.entity.RolesEntity;

import java.util.List;

public interface IRolesService {
    public List<RolesEntity> getAllRoles(Long id);

    public RolesEntity saveRole(RolesEntity role);

    public String deleteRole(Long id);
}
