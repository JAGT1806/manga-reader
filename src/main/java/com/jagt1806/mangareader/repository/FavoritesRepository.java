package com.jagt1806.mangareader.repository;

import com.jagt1806.mangareader.model.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
}