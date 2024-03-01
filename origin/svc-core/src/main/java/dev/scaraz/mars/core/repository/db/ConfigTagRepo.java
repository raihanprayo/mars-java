package dev.scaraz.mars.core.repository.db;

import dev.scaraz.mars.core.domain.Config;
import dev.scaraz.mars.core.domain.ConfigTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigTagRepo extends JpaRepository<ConfigTag, String> {
    boolean existsByNameIgnoreCase(String name);
    Optional<ConfigTag> findByNameIgnoreCase(String name);
}
