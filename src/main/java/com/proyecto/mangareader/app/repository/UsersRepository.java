package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.UsersEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    @Query("SELECT DISTINCT u FROM UsersEntity u LEFT JOIN u.roles r WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR r.rol LIKE %:role%) AND " +
            "(:enabled IS NULL OR u.enabled = :enabled)")

    List<UsersEntity> findByFilters(@Param("username") String username,
                                    @Param("email") String email,
                                    @Param("role") String role,
                                    @Param("enabled") Boolean enabled,
                                    Pageable pageable);

    @Query("SELECT COUNT(DISTINCT u) FROM UsersEntity u LEFT JOIN u.roles r WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR r.rol LIKE %:role%) AND " +
            "(:enabled IS NULL OR u.enabled = :enabled)")
    Long countByFilters(@Param("username") String username,
                        @Param("email") String email,
                        @Param("role") String role,
                        @Param("enabled") Boolean enabled);


    Optional<UsersEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<List<UsersEntity>> findByEnabledFalse();
}
