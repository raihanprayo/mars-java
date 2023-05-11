package dev.scaraz.mars.core.datasource.repo;

import dev.scaraz.mars.core.datasource.domain.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepo extends JpaRepository<AppConfig, Long> {
}
