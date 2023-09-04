package dev.scaraz.mars.core.repository.db;

import dev.scaraz.mars.core.domain.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ConfigRepo extends JpaRepository<Config, String> {
    List<Config> findAllByTagName(String tag);

    List<Config> findAllByTagNameNotIn(List<String> tags);

    default List<Config> findAllByTagNameNotIn(String... tags) {
        return findAllByTagNameNotIn(Arrays.asList(tags));
    }

}
