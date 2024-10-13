package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.FavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritesRepository extends JpaRepository<FavoritesEntity, Long> {
}
