package dev.scaraz.mars.core.v2.service.app.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.v2.config.event.app.ConfigUpdateEvent;
import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.domain.app.ConfigTag;
import dev.scaraz.mars.core.v2.repository.db.app.ConfigRepo;
import dev.scaraz.mars.core.v2.repository.db.app.ConfigTagRepo;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import dev.scaraz.mars.core.v2.util.ConfigConstants;
import dev.scaraz.mars.core.v2.web.dto.ConfigDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ApplicationEventPublisher eventPublisher;

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
    @Transactional
    public <T> void createIfNotExist(String key, T value, ConfigTag tag) {
        createIfNotExist(key, () -> value, tag);
    }

    @Override
    @Transactional
    public <T> void createIfNotExist(String key, Supplier<T> value, ConfigTag tag) {
        if (repo.existsById(key)) return;
        Config config = new Config();
        config.setKey(key);
        config.setTag(tag);
        config.setValue(value.get());
        save(config);
    }

    @Override
    @Transactional
    public void bulkCreate(String tagName, ConfigConstants.ConfigEntry<?>... entries) {
        if (tagRepo.existsByNameIgnoreCase(tagName)) return;
        ConfigTag tag = getOrCreateTag(tagName);
        for (ConfigConstants.ConfigEntry<?> entry : entries) {
            createIfNotExist(entry.getKey(), entry.getValue(), tag);
        }
    }

    @Override
    @Transactional
    public void bulkCreate(ConfigConstants.ConfigEntry<?>... entries) {
        for (ConfigConstants.ConfigEntry<?> entry : entries) {
            createIfNotExist(entry.getKey(), entry.getValue(), null);
        }
    }

    @Override
    @Transactional
    public Config update(ConfigDTO dto) {
        Config config = get(dto.getKey());
        config.setValue(dto);

        if (!config.getTag().getName().equals(dto.getTag())) {
            ConfigTag tag = tagRepo.findByNameIgnoreCase(dto.getTag())
                    .orElseThrow(() -> NotFoundException.entity(ConfigTag.class, "name", dto.getTag()));
            config.setTag(tag);
        }

        Config save = save(config);
        try {
            return save;
        }
        finally {
            eventPublisher.publishEvent(new ConfigUpdateEvent(save));
        }
    }

}
