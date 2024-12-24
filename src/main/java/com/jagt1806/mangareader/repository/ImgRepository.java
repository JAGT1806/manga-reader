package com.jagt1806.mangareader.repository;

import com.jagt1806.mangareader.model.Img;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImgRepository extends JpaRepository<Img, Long> {
    Optional<Img> findFirstByOrderById();

    Optional<Img> findByUrl(String url);
}