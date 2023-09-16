package dev.scaraz.mars.app.administration.service.app;

import dev.scaraz.mars.common.domain.ConfigDTO;
import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.domain.db.ConfigTag;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.ConfigEntry;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface ConfigService {
    Config save(Config c);

    ConfigTag save(ConfigTag c);

    ConfigTag getOrCreateTag(String tag);

    Config get(String key);

    Config get(Witel witel, String key);

    Map<String, Config> getBulkMap(String... keys);

    List<Config> getByTag(String tag);

    List<String> getTagList();

    @Transactional
    <T> void createIfNotExists(String key, T value, ConfigTag tag);

    @Transactional
    <T> void createIfNotExists(String key, Supplier<T> value, ConfigTag tag);

    @Transactional
    void bulkCreate(String tagName, ConfigEntry<?>... entries);

    @Transactional
    void bulkCreate(ConfigEntry<?>... entries);

    @Transactional
    Config update(ConfigDTO dto);
}
