package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.user.domain.db.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppConfigRepo extends JpaRepository<AppConfig, Long> {
    boolean existsByNameAndCategoryId(String name, String category);

    Optional<AppConfig> findByName(String name);

}
