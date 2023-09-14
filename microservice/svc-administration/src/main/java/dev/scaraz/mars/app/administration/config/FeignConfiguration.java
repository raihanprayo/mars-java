package dev.scaraz.mars.app.administration.config;

import feign.Feign;
import feign.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignConfiguration {

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }

    @Bean
    public Logger.Level feignLogLevel() {
        return Logger.Level.BASIC;
    }

}
