package dev.scaraz.mars.core.v2.service.app.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.domain.app.ConfigTag;
import dev.scaraz.mars.core.v2.repository.db.app.ConfigRepo;
import dev.scaraz.mars.core.v2.repository.db.app.ConfigTagRepo;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static dev.scaraz.mars.core.v2.util.ConfigConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepo repo;
    private final ConfigTagRepo tagRepo;

    @PostConstruct
    @Transactional
    public void initializeConfig() {
        forApplication();
        forAccount();
        forCredential();
        forJwt();
        forTelegram();
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

    private <T> void create(String key, T value, ConfigTag tag) {
        create(key, () -> value, tag);
    }

    private <T> void create(String key, Supplier<T> value, ConfigTag tag) {
        if (repo.existsById(key)) return;
        Config config = new Config();
        config.setKey(key);
        config.setTag(tag);
        config.setValue(value.get());
        save(config);
    }

    private void forApplication() {
        ConfigTag tag = getOrCreateTag(Tag.APPLICATION);
        create(APP_PENDING_CONFIRMATION_DRT, Duration.ofHours(1), tag);
        create(APP_CONFIRMATION_DRT, Duration.ofMinutes(30), tag);
        create(APP_ALLOW_AGENT_CREATE_TICKET_BOOL, false, tag);
        create(APP_USER_REGISTRATION_APPROVAL_BOOL, true, tag);
    }

    private void forAccount() {
        ConfigTag tag = getOrCreateTag(Tag.ACCOUNT);
        create(ACC_EXPIRED_BOOL, true, tag);
        create(ACC_EXPIRED_DRT, Duration.ofDays(365), tag);
    }

    private void forCredential() {
        ConfigTag tag = getOrCreateTag(Tag.CREDENTIAL);

        create(CRD_DEFAULT_PASSWORD_ALGO_STR, "bcrypt", tag);
        create(CRD_DEFAULT_PASSWORD_ITERATION_INT, 24_200, tag);
        create(CRD_DEFAULT_PASSWORD_SECRET_STR, () -> {
            Random random = new Random();
            byte[] randomSecret = new byte[16];
            random.nextBytes(randomSecret);
            return Base64.getEncoder().encodeToString(randomSecret);
        }, tag);
    }

    private void forJwt() {
        ConfigTag tag = getOrCreateTag(Tag.JWT);
        create(JWT_TOKEN_EXPIRED_DRT, Duration.ofHours(2), tag);
        create(JWT_TOKEN_REFRESH_EXPIRED_DRT, Duration.ofHours(12), tag);
    }

    private void forTelegram() {
        ConfigTag tag = getOrCreateTag(Tag.TELEGRAM);
        create(TG_START_CMD_ISSUE_COLUMN_INT, 3, tag);
    }

}
