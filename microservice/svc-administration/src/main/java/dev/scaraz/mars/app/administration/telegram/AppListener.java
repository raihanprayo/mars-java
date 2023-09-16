package dev.scaraz.mars.app.administration.telegram;

import dev.scaraz.mars.app.administration.domain.cache.FormRegistrationCache;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.app.administration.telegram.user.UserListener;
import dev.scaraz.mars.app.administration.telegram.user.UserNewRegistrationFlow;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import dev.scaraz.mars.telegram.annotation.context.ChatId;
import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@Slf4j
@TelegramBot
@RequiredArgsConstructor
public class AppListener {

    private final UserListener userListener;

    private final UserService userService;
    private final UserNewRegistrationFlow userNewRegistrationFlow;

    @TelegramMessage
    public SendMessage onMessage(@UserId long userId, @Text String text) {
        if (userNewRegistrationFlow.isInRegistration(userId)) {
            FormRegistrationCache cache = userNewRegistrationFlow.get(userId);
            userNewRegistrationFlow.answer(cache, text);

            switch (cache.getState()) {
                case NAME:
                    cache.setState(RegisterState.NIK);
                    userNewRegistrationFlow.save(cache);
                    return userNewRegistrationFlow.getPrompt(cache, RegisterState.NIK);
                case NIK:
                    cache.setState(RegisterState.PHONE);
                    userNewRegistrationFlow.save(cache);
                    return userNewRegistrationFlow.getPrompt(cache, RegisterState.PHONE);
                case PHONE:
                    cache.setState(RegisterState.WITEL);
                    userNewRegistrationFlow.save(cache);
                    return userNewRegistrationFlow.getPrompt(cache, RegisterState.WITEL);
//                case WITEL:
//                    if (cache.getWitel() == Witel.ROC)
//                        return userNewRegistrationFlow.summary(cache);
//                    else {
//                        cache.setState(RegisterState.REGION);
//                        userNewRegistrationFlow.save(cache);
//                        return userNewRegistrationFlow.getPrompt(cache, RegisterState.REGION);
//                    }
                case REGION:
                    return userNewRegistrationFlow.summary(cache);
            }
        }

        return null;
    }

    @TelegramCommand("/start")
    public SendMessage commandStart(@ChatId long chatId, @UserId long userId) {
        ChatSource chatSource = TelegramContextHolder.getChatSource();
        if (chatSource!= ChatSource.PRIVATE)
            throw new BadRequestException("command /start hanya bisa melalui private chat");

        log.info("Chat ID: {} | User ID: {}", chatId, userId);

        Optional<UserRepresentation> userOpt = userService.findByTelegramIdOpt(userId);
        if (userOpt.isEmpty())
            return userListener.register(chatId, userId);

        return null;
    }

}
