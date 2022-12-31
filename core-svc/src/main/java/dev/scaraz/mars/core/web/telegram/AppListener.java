package dev.scaraz.mars.core.web.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import dev.scaraz.mars.telegram.annotation.Text;
import dev.scaraz.mars.telegram.annotation.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class AppListener {

    private final ObjectMapper om;

    @TelegramMessage
    public void generalMessage(Update update) {
        try {
            log.info(om.writerFor(Update.class)
                    .writeValueAsString(update));
        }
        catch (JsonProcessingException e) {
        }
    }
}
