package org.ganesh.urlshortener.repository;

import org.ganesh.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortcode(String shortcode);
}
