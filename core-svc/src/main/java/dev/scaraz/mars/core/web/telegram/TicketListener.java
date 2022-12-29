package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.Text;
import dev.scaraz.mars.telegram.annotation.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class TicketListener {

    private final UserQueryService userQueryService;

    @TelegramCommand(commands = "/report", description = "Register new ticker/order")
    public void registerReport(@UserId long userId, @Text String textMessage) {
        userQueryService.findByTelegramId(userId);
    }

}
