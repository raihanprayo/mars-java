package dev.scaraz.mars.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
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
public class TelegramApiConfiguration implements ImportAware {

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(ObjectProvider<Class<? extends BotSession>> botSessionCtor) throws TelegramApiException {
        Class<? extends BotSession> type = botSessionCtor.getIfAvailable(() -> DefaultBotSession.class);
        return new TelegramBotsApi(type);
    }

}
