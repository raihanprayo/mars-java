package dev.scaraz.mars.v1.admin.repository.db.app;

import dev.scaraz.mars.v1.admin.domain.app.Config;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigRepo extends JpaRepository<Config, String> {
    List<Config> findAllByTagName(String tagName);
    Page<Config> findAllByTagName(String tagName, Pageable pageable);
}
