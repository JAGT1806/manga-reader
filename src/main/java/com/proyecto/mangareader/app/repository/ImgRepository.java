package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.ImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para la entidad de imagen.
 *
 * Extiende JpaRepository proporcionando operaciones CRUD estándar
 * y capacidades de paginación para la entidad ImgEntity.
 *
 * @see JpaRepository
 * @see ImgEntity
 */
@Repository
public interface ImgRepository extends JpaRepository<ImgEntity, Long> {
}
