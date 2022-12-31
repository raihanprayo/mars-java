package dev.scaraz.mars.common.exception.telegram;

public class TgUnauthorizedError extends TelegramError {

    public TgUnauthorizedError(long telegramId) {
        super("Unauthorized", String.format("Cannot find user with telegram id (%s)", telegramId));
    }
}
