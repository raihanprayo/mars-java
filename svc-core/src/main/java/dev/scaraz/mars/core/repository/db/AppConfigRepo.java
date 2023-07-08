package dev.scaraz.mars.core.repository.db;

import dev.scaraz.mars.core.domain.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepo extends JpaRepository<AppConfig, Long> {
}
