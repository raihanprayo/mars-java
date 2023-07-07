package dev.scaraz.mars.v1.admin.services.app.impl;

import dev.scaraz.mars.v1.admin.config.event.app.ConfigUpdateEvent;
import dev.scaraz.mars.v1.admin.domain.app.Config;
import dev.scaraz.mars.v1.admin.domain.app.ConfigTag;
import dev.scaraz.mars.v1.admin.repository.db.app.ConfigRepo;
import dev.scaraz.mars.v1.admin.repository.db.app.ConfigTagRepo;
import dev.scaraz.mars.v1.admin.services.app.ConfigService;
import dev.scaraz.mars.common.domain.ConfigDTO;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConfigServiceImpl implements ConfigService {

    private final ApplicationEventPublisher eventPublisher;

    private final ConfigRepo repo;
    private final ConfigTagRepo tagRepo;

    @Override
    public Config save(Config tag) {
        return repo.save(tag);
    }

    @Override
    public ConfigTag save(ConfigTag tag) {
        return tagRepo.save(tag);
    }

    @Override
    public Config get(String key) {
        return repo.findById(key)
                .orElseThrow(() -> NotFoundException.entity(Config.class, "key", key));
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
