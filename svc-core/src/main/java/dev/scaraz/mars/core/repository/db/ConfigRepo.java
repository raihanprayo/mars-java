package dev.scaraz.mars.core.repository.db;

import dev.scaraz.mars.core.domain.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepo extends JpaRepository<Config, String> {
}
