package dev.scaraz.mars.app.witel.config.telegram;

import org.telegram.telegrambots.bots.DefaultAbsSender;

public class TelegramApi extends DefaultAbsSender {


    public TelegramApi(TelegramApiOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
        return ((TelegramApiOptions) getOptions()).getToken();
    }
}
