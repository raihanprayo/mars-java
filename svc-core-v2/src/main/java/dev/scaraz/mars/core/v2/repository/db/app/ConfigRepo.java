package dev.scaraz.mars.core.v2.repository.db.app;

import dev.scaraz.mars.core.v2.domain.app.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigRepo extends JpaRepository<Config, String> {

    List<Config> findAllByTagName(String tag);

}
