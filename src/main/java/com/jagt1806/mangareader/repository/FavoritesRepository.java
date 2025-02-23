package com.jagt1806.mangareader.repository;

import com.jagt1806.mangareader.dto.favorite.FavoriteDTO;
import com.jagt1806.mangareader.model.Favorites;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
  void deleteAllByUserId_Id(Long id);

  Optional<List<Favorites>> findAllByUserId_Id(Long userIdId, Pageable pageable);

  long countFavoritesByUserId_Id(Long userIdId);

  void deleteByUserId_IdAndMangaId(Long userIdId, String mangaId);

  Boolean existsByUserId_IdAndMangaId(Long userIdId, String mangaId);
}
