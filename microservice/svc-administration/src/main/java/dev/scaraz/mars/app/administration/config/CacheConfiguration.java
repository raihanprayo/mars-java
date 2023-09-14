package dev.scaraz.mars.app.administration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Slf4j
@Configuration
@EnableCaching
@EnableRedisRepositories(
        basePackages = "dev.scaraz.mars.app.administration.repository.cache",
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
@RequiredArgsConstructor
public class CacheConfiguration {
}
