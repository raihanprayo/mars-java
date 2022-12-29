package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.TelegramBotProperties;
import dev.scaraz.mars.telegram.config.ProcessContextHolder;
import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.util.Optional;

/**
 * Webhook implementation of Telegram Bot Service.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
public class WebhookTelegramBotService extends TelegramBotService {

    @Getter
    private final TelegramWebhookBot client;

    public WebhookTelegramBotService(TelegramBotProperties botBuilder,
                                     TelegramBotsApi api) {

        this.client = createBot(botBuilder);
        try {
            api.registerBot(this.client, SetWebhook.builder()
                    .url(this.client.getBotPath())
                    .build());
        }
        catch (TelegramApiException e) {
            log.error("Cannot register Webhook with {}", botBuilder, e);
            throw new RuntimeException(e);
        }
    }

    private TelegramWebhookBot createBot(TelegramBotProperties botProperties) {
        WebhookTelegramBotService self = this;
        return new TelegramWebhookBot() {
            @Override
            public String getBotToken() {
                return botProperties.getToken();
            }

            @Override
            public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
                return Optional.ofNullable(self.onUpdateReceived(update))
                        .map(ctx -> {
                            ProcessContextHolder.clear();
                            return ctx.getResult();
                        })
                        .orElse(null);
            }

            @Override
            public String getBotPath() {
                return botProperties.getPath();
            }

            @Override
            public String getBotUsername() {
                return botProperties.getUsername();
            }
        };
    }

}
