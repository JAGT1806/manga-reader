package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.FavoritesEntity;
import com.proyecto.mangareader.app.entity.UsersEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface FavoritesRepository extends JpaRepository<FavoritesEntity, Long> {
    Optional<List<FavoritesEntity>> findAllByUserId(UsersEntity user);

    Optional<List<FavoritesEntity>> findAllByUserId_IdUser(Long userId, Pageable pageable);

    void deleteByUserId_IdUserAndIdManga(Long userId, String manga);

    boolean existsByUserIdIdUserAndIdManga(Long userId, String idManga);

    Optional<List<FavoritesEntity>> findAllByUserId_IdUser(Long userId);

    Long countFavoritesByUserId_IdUser(Long userId);

}
