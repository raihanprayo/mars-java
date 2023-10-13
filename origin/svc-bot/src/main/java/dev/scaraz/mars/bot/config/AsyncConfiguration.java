package dev.scaraz.mars.bot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@RequiredArgsConstructor

@EnableAsync
@Configuration
public class AsyncConfiguration {
}
