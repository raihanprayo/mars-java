package dev.scaraz.mars.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
@ImportAutoConfiguration({RedisAutoConfiguration.class})
@EnableRedisRepositories(
        basePackages = "dev.scaraz.mars.core.repository.cache",
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
        shadowCopy = RedisKeyValueAdapter.ShadowCopy.ON)
public class CacheConfiguration {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return RedisCacheManager.RedisCacheManagerBuilder::enableStatistics;
    }

    @Bean
    @Primary
    public JedisClientConfigurationBuilderCustomizer jedisClientConfigurationBuilderCustomizer() {
        return b -> b.readTimeout(Duration.ofSeconds(10))
                .connectTimeout(Duration.ofSeconds(10))
                .usePooling()
                .poolConfig(new JedisPoolConfig())
                .and()
                .clientName("mars-roc");
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties props) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setPassword(props.getPassword());
        config.setDatabase(props.getDatabase());
        config.setPort(props.getPort());
        config.setHostName(props.getHost());

        JedisConnectionFactory factory = new JedisConnectionFactory(config);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

}
