package dev.scaraz.mars.v1.core.repository;

import dev.scaraz.mars.v1.core.domain.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepo extends JpaRepository<AppConfig, Long> {
}
