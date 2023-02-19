package dev.scaraz.mars.common.exception.telegram;

import lombok.Getter;

public class TgError extends RuntimeException {

    @Getter
    private final String title;

    public TgError() {
        this("Telegram Error", null, null);
    }

    public TgError(String message) {
        this("Telegram Error", message, null);
    }

    public TgError(String title, String message) {
        this(title, message, null);
    }

    public TgError(String title, String message, Throwable cause) {
        super(message, cause);
        this.title = title;
    }

    public TgError(Throwable cause) {
        super(cause);
        this.title = "Telegram Error";
    }

    public String format() {
        return format(getMessage());
    }

    protected String format(String... contents) {
        String title = titleFormat() + "\n\n";
        return title + String.join("\n", contents);
    }

    protected String titleFormat() {
        return "*" + this.title + "*:";
    }

}
