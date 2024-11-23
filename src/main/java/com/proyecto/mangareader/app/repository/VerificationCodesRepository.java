package com.proyecto.mangareader.app.repository;

import com.proyecto.mangareader.app.entity.UsersEntity;
import com.proyecto.mangareader.app.entity.VerificationCodesEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodesRepository extends JpaRepository<VerificationCodesEntity, Long> {
    void deleteByUser(UsersEntity user);

    @Modifying
    @Query("DELETE FROM VerificationCodesEntity v WHERE v.expiryDate < :date")
    void deleteByExpiryDateBefore(LocalDateTime date);

    Optional<VerificationCodesEntity> findByCode(String code);

    void deleteByUserAndCodeStartingWith(UsersEntity user, String prefix);

    @Query("SELECT v FROM VerificationCodesEntity v WHERE v.user = :user AND LENGTH(v.code) =:length ")
    Optional<List<VerificationCodesEntity>> findByUserAndCodeLength(@Param("user") UsersEntity user, @Param("length") int length);
}
