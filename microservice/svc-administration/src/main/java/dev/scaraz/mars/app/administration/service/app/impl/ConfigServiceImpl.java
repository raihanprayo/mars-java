package dev.scaraz.mars.app.administration.service.app.impl;

import dev.scaraz.mars.app.administration.config.event.app.ConfigUpdateEvent;
import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.domain.db.ConfigTag;
import dev.scaraz.mars.app.administration.repository.db.ConfigRepo;
import dev.scaraz.mars.app.administration.repository.db.ConfigTagRepo;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.common.domain.ConfigDTO;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.utils.ConfigEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    public Map<String, Config> getBulkMap(String... keys) {
        return repo.findAllById(List.of(keys)).stream()
                .collect(Collectors.toMap(Config::getKey, c -> c));
    }

    @Override
    public List<Config> getByTag(String tag) {
        return repo.findAllByTagName(tag);
    }

    @Override
    public List<String> getTagList() {
        return tagRepo.findAll().stream()
                .map(ConfigTag::getName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public <T> void createIfNotExists(String key, T value, ConfigTag tag) {
        createIfNotExists(key, () -> value, tag);
    }

    @Override
    @Transactional
    public <T> void createIfNotExists(String key, Supplier<T> value, ConfigTag tag) {
        createOrUpdate(tag, new ConfigEntry<>(key, value));
    }

    private <T> void createOrUpdate(@Nullable ConfigTag tag, ConfigEntry<T> s) {
        String key = s.getKey();
        if (repo.existsById(key)) {
            Config config = get(key);
            boolean hasUpdate = false;

            if (StringUtils.isBlank(config.getDescription())) {
                config.setDescription(s.getDescription());
                hasUpdate = true;
            }

            if (tag != null) {
                if (config.getTag() != null && config.getTag().getId() != tag.getId()) {
                    log.info("CONFIG {} TAG UPDATE TO {}", config.getKey(), tag.getName());
                    config.setTag(tag);
                    hasUpdate = true;
                }
                else if (config.getTag() == null) {
                    config.setTag(tag);
                    hasUpdate = true;
                }
            }

            if (hasUpdate) save(config);
        }
        else {
            Config config = new Config();
            config.setKey(key);
            config.setTag(tag);
            config.setValue(s.getValue().get());
            config.setDescription(s.getDescription());
            save(config);
        }
    }

    @Override
    @Transactional
    public void bulkCreate(String tagName, ConfigEntry<?>... entries) {
        ConfigTag tag = getOrCreateTag(tagName);
        for (ConfigEntry<?> entry : entries) {
            createOrUpdate(tag, entry);
        }
    }

    @Override
    @Transactional
    public void bulkCreate(ConfigEntry<?>... entries) {
        for (ConfigEntry<?> entry : entries) {
            createOrUpdate(null, entry);
        }
    }

    @Override
    @Transactional
    public Config update(ConfigDTO dto) {
        Config config = get(dto.getKey());
        String oldValue = config.getValue();

        boolean anyUpdate = false;

        log.info("UPDATING APP CONFIG: {} -- {}", dto.getKey(), dto);

        if (!Objects.equals(config.getDescription(), dto.getDescription())) {
            config.setDescription(dto.getDescription());
            anyUpdate = true;
        }

        if (!config.getTag().getName().equals(dto.getTag())) {
            ConfigTag tag = tagRepo.findByNameIgnoreCase(dto.getTag())
                    .orElseThrow(() -> NotFoundException.entity(ConfigTag.class, "name", dto.getTag()));
            config.setTag(tag);
            anyUpdate = true;
        }

        config.setValue(dto);
        boolean valueChange = !Objects.equals(dto.getValue(), oldValue);
        if (!anyUpdate) anyUpdate = valueChange;

        if (anyUpdate) config = save(config);
        emitEventAsync(config, valueChange);
        return config;
    }

    @Async
    public void emitEventAsync(Config config, boolean valueChange) {
        eventPublisher.publishEvent(new ConfigUpdateEvent(config, valueChange));
    }

}
