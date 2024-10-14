package com.proyecto.mangareader.app.service.imp;

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
    public List<RolesEntity> getAllRoles(Long id) {
        if(id != null) {
            List<RolesEntity> unicRole = new ArrayList<>();
            unicRole.add(rolesRepository.findById(id).orElse(null));
            return unicRole;
        }
        return rolesRepository.findAll();
    }

    @Override
    public RolesEntity saveRole(RolesEntity role) {
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
}
