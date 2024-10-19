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

    @Query("SELECT u FROM UsersEntity u WHERE " +
        "(:username IS NULL OR u.username LIKE %:username%) AND " +
        "(:email IS NULL OR u.email LIKE %:email%) AND " +
        "(:role IS NULL OR u.rol.rol LIKE %:role%)")
    List<UsersEntity> findByFilters(@Param("username") String username,
                                    @Param("email") String email,
                                    @Param("role") String role,
                                    Pageable pageable);

    @Query("SELECT COUNT(u) FROM UsersEntity u WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR u.rol.rol LIKE %:role%)")
    Long countByFilters(@Param("username") String username,
                        @Param("email") String email,
                        @Param("role") String role);


    Optional<UsersEntity> findByEmail(String email);
}
