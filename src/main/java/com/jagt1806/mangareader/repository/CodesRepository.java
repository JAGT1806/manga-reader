package com.jagt1806.mangareader.repository;

import com.jagt1806.mangareader.model.Codes;
import com.jagt1806.mangareader.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CodesRepository extends JpaRepository<Codes, Long> {
    @Modifying
    @Query("DELETE FROM Codes v WHERE v.expiryDate < :date")
    void deleteByExpiryDateBefore(LocalDateTime date);

    Optional<Codes> findByCode(String code);

    @Query("SELECT v FROM Codes v WHERE v.user = :user AND LENGTH(v.code) =:length ")
    Optional<List<Codes>> findByUserAndCodeLength(@Param("user") Users user, @Param("length") int length);
}