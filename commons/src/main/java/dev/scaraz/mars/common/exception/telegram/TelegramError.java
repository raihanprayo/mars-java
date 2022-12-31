package dev.scaraz.mars.common.exception.telegram;

import lombok.Getter;
import lombok.Setter;

public class TelegramError extends RuntimeException {

    @Getter
    @Setter
    private String title;

    public TelegramError() {
        this("Telegram Error", null, null);
    }

    public TelegramError(String message) {
        this("Telegram Error", message, null);
    }

    public TelegramError(String title, String message) {
        this(title, message, null);
    }

    public TelegramError(String title, String message, Throwable cause) {
        super(message, cause);
        this.title = title;
    }

    public TelegramError(Throwable cause) {
        super(cause);
        this.title = "Telegram Error";
    }

}
