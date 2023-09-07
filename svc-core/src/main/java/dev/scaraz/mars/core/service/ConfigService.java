package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.ConfigDTO;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.Config;
import dev.scaraz.mars.core.domain.ConfigTag;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface ConfigService {
    Config save(Config c);

    ConfigTag save(ConfigTag c);

    ConfigTag getOrCreateTag(String tag);

    Config get(String key);

    Map<String, Config> getBulkMap(String... keys);

    List<Config> getByTag(String tag);

    List<String> getTagList();

    @Transactional
    <T> void createIfNotExists(String key, T value, ConfigTag tag);

    @Transactional
    <T> void createIfNotExists(String key, Supplier<T> value, ConfigTag tag);

    @Transactional
    void bulkCreate(String tagName, ConfigConstants.ConfigEntry<?>... entries);

    @Transactional
    void bulkCreate(ConfigConstants.ConfigEntry<?>... entries);

    @Transactional
    Config update(ConfigDTO dto);
}
