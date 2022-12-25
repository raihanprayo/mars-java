package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.TelegramBotProperties;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

/**
 * Webhook implementation of Telegram Bot Service.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
public class WebhookTelegramBotService extends TelegramBotService {

    private final String username;
    private final String token;
    private final String path;
    private final TelegramWebhookBot client;

    public WebhookTelegramBotService(
            TelegramBotProperties botBuilder, TelegramBotsApi api, ConfigurableBeanFactory configurableBeanFactory
    ) {
        super(api, configurableBeanFactory);
        username = botBuilder.getUsername();
        token = botBuilder.getToken();
        path = botBuilder.getPath();
        client = new TelegramBotWebhookImpl();
        try {
            api.registerBot((LongPollingBot) client);
        } catch (TelegramApiException e) {
            log.error("Can not register Long Polling with {}", botBuilder, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TelegramWebhookBot getClient() {
        return client;
    }

    private class TelegramBotWebhookImpl extends TelegramWebhookBot {

        @Override
        public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
            return updateProcess(update).orElse(null);
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }

        @Override
        public String getBotPath() {
            return path;
        }
    }
}
