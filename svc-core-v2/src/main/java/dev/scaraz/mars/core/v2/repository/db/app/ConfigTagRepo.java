package dev.scaraz.mars.core.v2.repository.db.app;

import dev.scaraz.mars.core.v2.domain.app.ConfigTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigTagRepo extends JpaRepository<ConfigTag, Integer> {
    Optional<ConfigTag> findByNameIgnoreCase(String name);
}
