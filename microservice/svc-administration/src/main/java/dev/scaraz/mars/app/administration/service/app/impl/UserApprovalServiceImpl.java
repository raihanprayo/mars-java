package dev.scaraz.mars.app.administration.service.app.impl;

import dev.scaraz.mars.app.administration.config.event.app.CacheExpiredEvent;
import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.domain.db.UserApproval;
import dev.scaraz.mars.app.administration.repository.db.UserApprovalRepo;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.app.administration.service.app.UserApprovalService;
import dev.scaraz.mars.app.administration.service.app.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApprovalServiceImpl implements UserApprovalService {
    private static final String NAMESPACE = "user:reg:approval";

    private final ApplicationContext applicationContext;

    private final UserApprovalRepo repo;

    private final ConfigService configService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public UserApproval save(UserApproval approval) {
        UserApproval a = repo.save(approval);
        String key = namespace(a.getId());

        Duration duration = configService.get(Config.USER_REGISTRATION_APPROVAL_DRT).getAsDuration();
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, a.getNo(), duration);
        return a;
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
        redisTemplate.delete(namespace(id));
    }

    @Override
    public boolean isInApprovalWaitList(long id) {
        return repo.existsByTelegramId(id);
    }

    @Async
    @EventListener(CacheExpiredEvent.class)
    public void onCacheExpired(CacheExpiredEvent event) {
        if (!event.getNamespace().equals(NAMESPACE)) return;

        applicationContext.getBean(UserService.class)
                .deleteRegistration(event.getValue());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        for (String id : repo.findAllId()) {
            String key = namespace(id);
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) continue;

            applicationContext.getBean(UserService.class)
                    .deleteRegistration(id);
        }
    }

    private String namespace(String id) {
        return String.format("%s:%s", NAMESPACE, id);
    }
}
