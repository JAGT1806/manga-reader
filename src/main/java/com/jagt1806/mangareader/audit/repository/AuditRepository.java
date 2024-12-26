package com.jagt1806.mangareader.audit.repository;

import com.jagt1806.mangareader.audit.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
}