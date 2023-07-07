package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.TelegramBotProperties;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Webhook implementation of Telegram Bot Service.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
public class WebhookTelegramBotService extends TelegramBotService {

    @Getter
    private final TelegramWebhookBot client;
    private final TelegramBotsApi api;

    public WebhookTelegramBotService(TelegramBotProperties botBuilder,
                                     TelegramBotsApi api) {
        this.api = api;
        this.client = createBot(botBuilder);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws TelegramApiException {
        log.info("Webhook Bot Starting");
        api.registerBot(client, SetWebhook.builder()
                .url(client.getBotPath())
                .build());
    }

    private TelegramWebhookBot createBot(TelegramBotProperties botProperties) {
        return new TelegramWebhookBot() {

            private final String token = botProperties.getToken();
            private final String path = botProperties.getPath();
            private final String username = botProperties.getUsername();

            @Override
            public String getBotToken() {
                return this.token;
            }

            @Override
            public String getBotPath() {
                return this.path;
            }

            @Override
            public String getBotUsername() {
                return this.username;
            }

            @Override
            public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
                WebhookTelegramBotService.this.onUpdateReceived(update);
                try {
                    TelegramProcessContext ctx = TelegramContextHolder.get();
                    return ctx.getResult();
                }
                catch (IllegalStateException ex) {
                    return null;
                }
                finally {
                    TelegramContextHolder.clear();
                }
            }
        };
    }

}
