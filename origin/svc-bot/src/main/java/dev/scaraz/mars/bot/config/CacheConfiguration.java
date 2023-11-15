package dev.scaraz.mars.bot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Slf4j
@RequiredArgsConstructor

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@EnableRedisRepositories(
        basePackages = "dev.scaraz.mars.bot.repository",
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
        shadowCopy = RedisKeyValueAdapter.ShadowCopy.ON)
public class CacheConfiguration {
}
