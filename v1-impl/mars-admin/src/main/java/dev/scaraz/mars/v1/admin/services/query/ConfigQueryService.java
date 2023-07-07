package dev.scaraz.mars.v1.admin.services.query;

import dev.scaraz.mars.v1.admin.domain.app.Config;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConfigQueryService extends BaseQueryService<Config> {
    List<Config> findAllByTag(String tag);

    Page<Config> findAllByTag(String tag, Pageable pageable);
}
