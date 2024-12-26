package com.jagt1806.mangareader.repository;

import com.jagt1806.mangareader.model.Roles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByRol(String rol);

    Optional<List<Roles>> findByRolContaining(String role, Pageable pageable);

    long countByRolContaining(String rol);

    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE role_id = :roleId ", nativeQuery = true)
    void deleteUserRolesByRoleId(@Param("roleId") Long roleId);
}