package dev.scaraz.mars.telegram;

import dev.scaraz.mars.telegram.service.LongPollingTelegramBotService;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.service.WebhookTelegramBotService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;

/**
 * Telegram Bot type.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum TelegramBotType {
    /**
     * Use long polling mode.
     */
    LONG_POLLING,

    /**
     * Use webhook mode.
     */
    WEBHOOK;
}
