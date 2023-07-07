package dev.scaraz.mars.v1.admin.repository.db.app;

import dev.scaraz.mars.v1.admin.domain.app.ConfigTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigTagRepo extends JpaRepository<ConfigTag, String> {
    Optional<ConfigTag> findByNameIgnoreCase(String name);
}
