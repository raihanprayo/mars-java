package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import dev.scaraz.mars.telegram.annotation.Text;
import dev.scaraz.mars.telegram.annotation.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class AppListener {

    @TelegramMessage
    public void generalMessage(long userId, @Text String text) {
        log.info("Incoming message USERID={} TEXT={}", userId, text);
    }
}
