package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.TelegramBotProperties;
import dev.scaraz.mars.telegram.TelegramProperties;
import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import dev.scaraz.mars.telegram.service.LongPollingTelegramBotService;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.service.WebhookTelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Configuration
public class TelegramBotConfiguration {

    private final TelegramBotsApi api;

    private final TelegramProperties telegramProperties;

    @Bean
    @ConditionalOnMissingBean(TelegramBotService.class)
    public TelegramBotService telegramBotService() {
        TelegramBotProperties botProperties = TelegramBotProperties.builder()
                .token(telegramProperties.getToken())
                .username(telegramProperties.getName())
                .maxThreads(telegramProperties.getMaxThreads())
                .build();

        switch (telegramProperties.getType()) {
            case WEBHOOK:
                return new WebhookTelegramBotService(botProperties, api);
            case LONG_POLLING:
                return new LongPollingTelegramBotService(botProperties, api);
        }

        throw new IllegalStateException(String.format("Unhandled bot type %s", telegramProperties.getType()));
    }

}
