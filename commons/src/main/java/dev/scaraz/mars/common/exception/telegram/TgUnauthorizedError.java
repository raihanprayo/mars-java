package dev.scaraz.mars.common.exception.telegram;

public class TgUnauthorizedError extends TgError {

    public TgUnauthorizedError(long telegramId) {
        super("Unauthorized User", String.format("Cannot find user with telegram id (%s)", telegramId));
    }
}
