package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
}
