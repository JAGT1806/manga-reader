package com.jagt1806.mangareader.repository;

import com.jagt1806.mangareader.model.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM Users u LEFT JOIN u.roles r WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR r.rol LIKE %:rol%) AND " +
            "(:enabled IS NULL OR u.enabled = :enabled)")
    List<Users> findByFilters(@Param("username") String username,
                              @Param("email") String email,
                              @Param("role") String role,
                              @Param("enabled") Boolean enabled,
                              Pageable pageable);

    @Query("SELECT COUNT(DISTINCT u) FROM Users u LEFT JOIN u.roles r WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR r.rol LIKE %:rol%) AND " +
            "(:enabled IS NULL OR u.enabled = :enabled)")
    Long countByFilters(@Param("username") String username,
                        @Param("email") String email,
                        @Param("role") String role,
                        @Param("enabled") Boolean enabled);

    Optional<List<Users>> findByEnabledIsFalse();
}