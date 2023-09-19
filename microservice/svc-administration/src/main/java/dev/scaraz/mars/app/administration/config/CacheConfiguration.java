package dev.scaraz.mars.app.administration.config;

import dev.scaraz.mars.app.administration.config.event.app.CacheExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
@EnableRedisRepositories(
        basePackages = "dev.scaraz.mars.app.administration.repository.cache",
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@RequiredArgsConstructor
public class CacheConfiguration {
    public static final String
            CACHE_KEYCLOAK_USER = "kc:user",
            CACHE_KEYCLOAK_CLIENT = "kc:client";

    private final ApplicationEventPublisher eventPublisher;

    @Bean
    public RedisMessageListenerContainer expiredListenerContainer(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener((message, pattern) -> {
            String key = new String(message.getBody());
            String ns = key.substring(0, key.lastIndexOf(":"));
            String value = key.substring(key.lastIndexOf(":") + 1);

            eventPublisher.publishEvent(new CacheExpiredEvent(this, key, ns, value));
        }, new PatternTopic("__keyevent@*__:expired"));
        return container;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return b -> b
                .withCacheConfiguration(CACHE_KEYCLOAK_CLIENT,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(CACHE_KEYCLOAK_USER,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));
    }

}
