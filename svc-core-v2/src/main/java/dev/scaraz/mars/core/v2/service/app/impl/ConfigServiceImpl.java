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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import java.util.List;

import static dev.scaraz.mars.core.v2.util.ConfigConstants.Tag;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepo repo;
    private final ConfigTagRepo tagRepo;

    @PostConstruct
    @Transactional
    public void init() {
        ConfigTag appTag = getOrCreateTag(ConfigConstants.Tag.APPLICATION);
        ConfigTag credentialTag = getOrCreateTag(ConfigConstants.Tag.CREDENTIAL);
        ConfigTag telegramTag = getOrCreateTag(ConfigConstants.Tag.TELEGRAM);

        for (String key : ConfigConstants.DEFAULTS.keySet()) {
            if (repo.existsById(key)) continue;
            ConfigConstants.Tupple<?> tupple = ConfigConstants.DEFAULTS.get(key);
            Config config = new Config();
            config.setKey(key);
            config.setValue(tupple.getValue());

            switch (tupple.getTag()) {
                case Tag.APPLICATION:
                    config.setTag(appTag);
                    break;
                case Tag.TELEGRAM:
                    config.setTag(telegramTag);
                    break;
                case Tag.CREDENTIAL:
                    config.setTag(credentialTag);
                    break;
            }

            log.info("CREATE NEW CONFIG {} of TYPE {}", key, tupple.getValue().getClass().getCanonicalName());
            save(config);
        }
    }

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

}
