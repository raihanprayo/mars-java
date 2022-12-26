package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class AppListener {

    @TelegramMessage
    public void generalMessage(Message message) {
        log.info("Incoming message {}", message);
    }
}
