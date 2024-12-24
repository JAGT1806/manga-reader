package com.jagt1806.mangareader.repository;

import com.jagt1806.mangareader.model.Codes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodesRepository extends JpaRepository<Codes, Long> {
}