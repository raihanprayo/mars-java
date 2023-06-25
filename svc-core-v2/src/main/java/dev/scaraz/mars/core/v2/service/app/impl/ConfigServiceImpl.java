package dev.scaraz.mars.core.v2.service.app.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.domain.app.ConfigTag;
import dev.scaraz.mars.core.v2.repository.db.app.ConfigRepo;
import dev.scaraz.mars.core.v2.repository.db.app.ConfigTagRepo;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import dev.scaraz.mars.core.v2.util.ConfigConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepo repo;
    private final ConfigTagRepo tagRepo;
    @Override
    public Config save(Config c) {
        return repo.save(c);
    }


    @Override
    public ConfigTag save(ConfigTag c) {
        return tagRepo.save(c);
    }

    @Override
    public ConfigTag getOrCreateTag(String tag) {
        return tagRepo.findByNameIgnoreCase(tag)
                .orElseGet(() -> {
                    log.info("CREATE NEW CONFIG TAG {}", tag);
                    return save(ConfigTag.builder()
                            .name(tag)
                            .build());
                });
    }

    @Override
    public Config get(String key) {
        return repo.findById(key)
                .orElseThrow(() -> NotFoundException.entity(Config.class, "key", key));
    }

    @Override
    public List<Config> getByTags(String tag) {
        return repo.findAllByTagName(tag);
    }

    @Override
    public  <T> void createIfNotExist(String key, T value, ConfigTag tag) {
        createIfNotExist(key, () -> value, tag);
    }

    @Override
    public  <T> void createIfNotExist(String key, Supplier<T> value, ConfigTag tag) {
        if (repo.existsById(key)) return;
        Config config = new Config();
        config.setKey(key);
        config.setTag(tag);
        config.setValue(value.get());
        save(config);
    }

    @Override
    public void bulkCreate(String tagName, ConfigConstants.ConfigEntry<?>... entries) {
        if (tagRepo.existsByNameIgnoreCase(tagName)) return;
        ConfigTag tag = getOrCreateTag(tagName);
        for (ConfigConstants.ConfigEntry<?> entry : entries)
            createIfNotExist(entry.getKey(), entry.getValue(), tag);
    }

    @Override
    public void bulkCreate(ConfigConstants.ConfigEntry<?>... entries) {
        for (ConfigConstants.ConfigEntry<?> entry : entries)
            createIfNotExist(entry.getKey(), entry.getValue(), null);
    }

}
