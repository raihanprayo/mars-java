package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.core.service.credential.UserAuthService;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.annotation.Command;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.Text;
import dev.scaraz.mars.telegram.util.MessageEntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Locale;

import static dev.scaraz.mars.core.config.WebConfiguration.LOCALE_EN;
import static dev.scaraz.mars.core.config.WebConfiguration.LOCALE_ID;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class UserListener {

    private final UserAuthService authService;

    private final UserService userService;

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

    @TelegramCommand(commands = "/setting")
    public SendMessage setting(@TgAuth dev.scaraz.mars.core.domain.credential.User user,
                               @Text String text
    ) {
        if (checkSettingFormat(text)) {
            String[] split = text.split("[ =]");
            String cmd = split[0];
            String value = split[1];

            if (cmd.equalsIgnoreCase("lang")) {
                boolean isEn = value.equalsIgnoreCase("en");
                boolean isIdn = value.equalsIgnoreCase("id");
                if (isEn || isIdn) {
                    if (isIdn) user.getSetting().setLang(LOCALE_ID);
                    else user.getSetting().setLang(LOCALE_EN);

                    userService.save(user.getSetting());
                    return SendMessage.builder()
                            .chatId(user.getTelegramId())
                            .text("OK")
                            .build();
                }
                else {
                    return SendMessage.builder()
                            .chatId(user.getTelegramId())
                            .text("Accepted language only en/id (ignore-case)")
                            .build();
                }
            }
        }

        return null;
    }


    private boolean checkSettingFormat(String text) {
        return text.split("[ =]").length == 2;
    }
}
