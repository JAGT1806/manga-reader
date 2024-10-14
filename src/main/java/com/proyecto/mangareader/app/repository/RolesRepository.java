package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolesRepository extends JpaRepository<RolesEntity, Long> {
    // Busqueda por role
    Optional<List<RolesEntity>> findByRolContaining(String role);
}
