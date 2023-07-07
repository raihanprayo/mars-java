package dev.scaraz.mars.core.v2.service.app;

import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.domain.app.ConfigTag;
import dev.scaraz.mars.core.v2.util.ConfigConstants;
import dev.scaraz.mars.core.v2.web.dto.ConfigDTO;

import java.util.function.Supplier;

public interface ConfigService {
    Config save(Config c);

    ConfigTag save(ConfigTag c);

    ConfigTag getOrCreateTag(String tag);

    Config get(String key);

    <T> void createIfNotExist(String key, T value, ConfigTag tag);

    <T> void createIfNotExist(String key, Supplier<T> value, ConfigTag tag);

    void bulkCreate(String tagName, ConfigConstants.ConfigEntry<?>... entries);

    void bulkCreate(ConfigConstants.ConfigEntry<?>... entries);

    Config update(ConfigDTO dto);
}
