package dev.scaraz.mars.telegram;

import dev.scaraz.mars.telegram.service.LongPollingTelegramBotService;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.service.WebhookTelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Configuration which will be used to initialize telegram bot api.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
@RequiredArgsConstructor

@ConditionalOnProperty(prefix = "telegram",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@ComponentScan("dev.scaraz.mars.telegram")
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramConfiguration implements ImportAware {

    private final ConfigurableBeanFactory configurableBeanFactory;

    private final TelegramProperties telegramProperties;

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {
    }

    /**
     * Telegram Bots API.
     */
    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    /**
     * Telegram Bot Service to dispatch messages.
     */
    @Bean
    public TelegramBotService telegramBotService(TelegramBotsApi api) {
        log.info("Initializing Bot");

        TelegramBotType type = telegramProperties.getType();
        TelegramBotProperties botProperties = TelegramBotProperties.builder()
                .token(telegramProperties.getToken())
                .username(telegramProperties.getName())
                .maxThreads(telegramProperties.getMaxThreads())
                .build();

        switch (type) {
            case LONG_POLLING:
                return new LongPollingTelegramBotService(botProperties, api, configurableBeanFactory);
            case WEBHOOK:
                return new WebhookTelegramBotService(botProperties, api, configurableBeanFactory);
        }

        throw new RuntimeException("invalid bot type");
    }

    /**
     * Bean post-processor to process Telegram Bot API annotations.
     */
    @Bean
    public TelegramBeanPostProcessor telegramBeanPostProcessor(TelegramBotService telegramBotService) {
        return new TelegramBeanPostProcessor(telegramBotService, configurableBeanFactory);
    }

}
