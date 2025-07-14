package org.ganesh.urlshortener.repository;

import org.ganesh.urlshortener.model.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
}
