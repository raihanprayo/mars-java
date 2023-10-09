package dev.scaraz.mars.app.administration.repository.db;

import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.common.tools.enums.Witel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ConfigRepo extends JpaRepository<Config, String> {

    List<Config> findAllByWitelIsNullOrWitelEquals(Witel witel);
    List<Config> findAllByTagNameAndWitelIsNullOrWitelEquals(String tagName, Witel witel);

    List<Config> findAllByTagName(String tag);

    List<Config> findAllByTagNameNotIn(List<String> tags);

    default List<Config> findAllByTagNameNotIn(String... tags) {
        return findAllByTagNameNotIn(Arrays.asList(tags));
    }

}
