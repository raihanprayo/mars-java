package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.core.service.credential.UserAuthService;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.util.MessageEntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class UserListener {

    private final UserAuthService authService;

    @TelegramCommand(commands = "/register")
    public SendMessage register(User user, Message message) {
        log.info("User {}", user);

        if (!authService.isUserRegistered(user.getId())) {
            for (MessageEntity entity : message.getEntities()) {
                if (!MessageEntityType.is(entity, MessageEntityType.BOT_COMMAND)) continue;

                return authService.registerFromBot(entity, message)
                        .build();
            }
        }

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("user already registered")
                .build();
    }

}
