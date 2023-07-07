package dev.scaraz.mars.core.v2.query.app;

import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.query.NonSpecificationQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConfigQueryService extends NonSpecificationQueryService<Config> {
    List<Config> findAllByTag(String tag);

    Page<Config> findAllByTag(String tag, Pageable pageable);
}
