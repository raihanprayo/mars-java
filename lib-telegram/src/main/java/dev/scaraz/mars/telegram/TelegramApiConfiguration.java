package dev.scaraz.mars.telegram;

import dev.scaraz.mars.telegram.service.LongPollingTelegramBotService;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.service.WebhookTelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
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
@ComponentScan(basePackages = "dev.scaraz.mars.telegram")
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramApiConfiguration {

    private final TelegramProperties telegramProperties;

    @Bean
    public TelegramBotsApi telegramBotsApi(ObjectProvider<Class<? extends BotSession>> botSessionCtor) throws TelegramApiException {
        Class<? extends BotSession> type = botSessionCtor.getIfAvailable(() -> DefaultBotSession.class);
        return new TelegramBotsApi(type);
    }

    private TelegramBotProperties getBotProperties() {
        return TelegramBotProperties.builder()
                .token(telegramProperties.getToken())
                .username(telegramProperties.getName())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(TelegramBotService.class)
    @ConditionalOnProperty(prefix = "telegram",
            name = "type",
            havingValue = "long_polling",
            matchIfMissing = true)
    public TelegramBotService longPollingTelegramBotService(TelegramBotsApi api) {
        VirtualThreadTaskExecutor executor = new VirtualThreadTaskExecutor("telegram-");
        return new LongPollingTelegramBotService(getBotProperties(), api, executor);
    }

    @Bean
    @ConditionalOnMissingBean(TelegramBotService.class)
    @ConditionalOnProperty(prefix = "telegram",
            name = "type",
            havingValue = "webhook")
    public TelegramBotService webhookTelegramBotService(TelegramBotsApi api) {
        return new WebhookTelegramBotService(getBotProperties(), api);
    }


}
