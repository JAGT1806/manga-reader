package com.jagt1806.mangareader.repository;

import com.jagt1806.mangareader.model.Roles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
}