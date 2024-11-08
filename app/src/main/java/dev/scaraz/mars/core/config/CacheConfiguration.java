package dev.scaraz.mars.core.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.common.utils.CacheConstant;
import dev.scaraz.mars.core.tools.CacheExpireListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@EnableRedisRepositories(
        basePackages = "dev.scaraz.mars.core.repository.cache",
        considerNestedRepositories = true,
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
        shadowCopy = RedisKeyValueAdapter.ShadowCopy.ON)
public class CacheConfiguration {

    @Bean
    public RedisMessageListenerContainer expiredListenerContainer(
            List<CacheExpireListener> listeners,
            RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener((message, pattern) -> {
            String key = new String(message.getBody());
            String ns = key.substring(0, key.lastIndexOf(":"));
            String suffix = key.substring(key.lastIndexOf(":") + 1);

            listeners.stream().filter(lst -> lst.getNamespace().equals(ns))
                    .findFirst()
                    .ifPresent(listener -> {
                        log.info("Expired Cache Key: {}", key);
                        listener.onExpired(suffix);
                    });
        }, new PatternTopic("__keyevent@*__:expired"));
        return container;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(ObjectMapper objectMapper) {
        return builder -> {
            ObjectMapper copy = objectMapper.copy()
                    .activateDefaultTyping(
                            objectMapper.getPolymorphicTypeValidator(),
                            ObjectMapper.DefaultTyping.EVERYTHING,
                            JsonTypeInfo.As.PROPERTY
                    );

            RedisSerializationContext.SerializationPair<Object> serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(copy));

            builder.withCacheConfiguration(CacheConstant.ISSUES_KEYBOARD, RedisCacheConfiguration.defaultCacheConfig()
                    .disableCachingNullValues()
                    .entryTtl(Duration.ofMinutes(5)));
        };
    }

}
