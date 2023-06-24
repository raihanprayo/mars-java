package dev.scaraz.mars.core.v2.service.app;

import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.domain.app.ConfigTag;

import java.util.List;

public interface ConfigService {
    Config save(Config c);

    ConfigTag save(ConfigTag c);

    ConfigTag getOrCreateTag(String tag);

    Config get(String key);

    List<Config> getByTags(String tag);
}
