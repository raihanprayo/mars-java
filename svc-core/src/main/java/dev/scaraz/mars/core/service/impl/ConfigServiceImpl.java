package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.common.domain.ConfigDTO;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.config.event.app.ConfigUpdateEvent;
import dev.scaraz.mars.core.domain.Config;
import dev.scaraz.mars.core.domain.ConfigTag;
import dev.scaraz.mars.core.repository.db.ConfigRepo;
import dev.scaraz.mars.core.repository.db.ConfigTagRepo;
import dev.scaraz.mars.core.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
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
    public List<Config> getByTag(String tag) {
        return repo.findAllByTagName(tag);
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
//        if (tagRepo.existsByNameIgnoreCase(tagName)) return;
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
        log.info("UPDATING APP CONFIG: {} -- {}", dto.getKey(), dto);
        config.setValue(dto);

        if (!config.getTag().getName().equals(dto.getTag())) {
            ConfigTag tag = tagRepo.findByNameIgnoreCase(dto.getTag())
                    .orElseThrow(() -> NotFoundException.entity(ConfigTag.class, "name", dto.getTag()));
            config.setTag(tag);
        }

        Config save = save(config);
        emitEventAsync(save);
        return save;
    }

    @Async
    public void emitEventAsync(Config config) {
        eventPublisher.publishEvent(new ConfigUpdateEvent(config));
    }

}
