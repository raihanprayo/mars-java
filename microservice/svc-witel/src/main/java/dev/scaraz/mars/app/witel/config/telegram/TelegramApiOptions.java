package dev.scaraz.mars.app.witel.config.telegram;

import org.telegram.telegrambots.bots.DefaultBotOptions;

public class TelegramApiOptions extends DefaultBotOptions {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
