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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.List;

import static dev.scaraz.mars.telegram.util.TelegramUtil.TELEGRAM_EXECUTOR;

@Slf4j
@RequiredArgsConstructor

@EnableAsync
@Configuration
public class TelegramBotConfiguration {

    private final TelegramBotsApi api;

    private final TelegramProperties telegramProperties;

    @Bean
    @ConditionalOnMissingBean(TelegramBotService.class)
    public TelegramBotService telegramBotService(
            @Autowired(required = false)
            @Qualifier(TELEGRAM_EXECUTOR) TaskExecutor taskExecutor
    ) {
        TelegramBotProperties botProperties = TelegramBotProperties.builder()
                .token(telegramProperties.getToken())
                .username(telegramProperties.getName())
                .maxThreads(telegramProperties.getMaxThreads())
                .build();

        switch (telegramProperties.getType()) {
            case WEBHOOK:
                return new WebhookTelegramBotService(botProperties, api);
            case LONG_POLLING:
                return new LongPollingTelegramBotService(botProperties, api, taskExecutor);
        }

        throw new IllegalStateException(String.format("Unhandled bot type %s", telegramProperties.getType()));
    }

    @Bean(TELEGRAM_EXECUTOR)
    @ConditionalOnProperty(prefix = "telegram", name = "type", havingValue = "long_polling")
    public TaskExecutor updateExecutor() {
        ThreadPoolTaskExecutor exc = new ThreadPoolTaskExecutor();
        exc.setCorePoolSize(3);
        exc.setMaxPoolSize(100);
        exc.setQueueCapacity(1000);
        exc.setThreadNamePrefix("TG-UPDATE-");
        return exc;
    }

}
