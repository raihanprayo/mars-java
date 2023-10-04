package dev.scaraz.mars.app.administration.telegram;

import dev.scaraz.mars.app.administration.domain.cache.FormUserRegistrationCache;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.app.administration.telegram.ticket.TicketRegistrationFlow;
import dev.scaraz.mars.app.administration.telegram.user.UserListener;
import dev.scaraz.mars.app.administration.telegram.user.UserRegistrationFlow;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import dev.scaraz.mars.telegram.annotation.context.CallbackData;
import dev.scaraz.mars.telegram.annotation.context.ChatId;
import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@Slf4j
@TelegramBot
@RequiredArgsConstructor
public class AppListener {

    private final UserListener userListener;

    private final UserService userService;
    private final UserRegistrationFlow userRegistrationFlow;
    private final TicketRegistrationFlow ticketRegistrationFlow;

    @TelegramMessage
    public SendMessage onMessage(@UserId long userId, @Text String text) {
        if (userRegistrationFlow.isInRegistration(userId)) {
            FormUserRegistrationCache cache = userRegistrationFlow.get(userId);
            userRegistrationFlow.answer(cache, text);

            switch (cache.getState()) {
                case NAME:
                    cache.setState(RegisterState.NIK);
                    userRegistrationFlow.save(cache);
                    return userRegistrationFlow.getPrompt(cache, RegisterState.NIK);
                case NIK:
                    cache.setState(RegisterState.PHONE);
                    userRegistrationFlow.save(cache);
                    return userRegistrationFlow.getPrompt(cache, RegisterState.PHONE);
                case PHONE:
                    cache.setState(RegisterState.WITEL);
                    userRegistrationFlow.save(cache);
                    return userRegistrationFlow.getPrompt(cache, RegisterState.WITEL);
                case REGION:
                    return userRegistrationFlow.summary(cache);
            }
        }

        return null;
    }

    @TelegramCallbackQuery
    public BotApiMethod<?> onCallbackQuery(
            @UserId long userId,
            @CallbackData String data
    ) {
        if (userRegistrationFlow.isInRegistration(userId)) {
            if (data.startsWith("WITEL_")) {
                FormUserRegistrationCache cache = userRegistrationFlow.get(userId);
                userRegistrationFlow.answer(cache, data);

                if (cache.getWitel() == Witel.ROC)
                    return userRegistrationFlow.summary(cache);

                cache.setState(RegisterState.REGION);
                userRegistrationFlow.save(cache);
                return userRegistrationFlow.getPrompt(cache, RegisterState.REGION);
            }
        }
        else if (data.startsWith(AppConstants.Telegram.REPORT_ISSUE)) {

        }


        return null;
    }

    @TelegramCommand("/start")
    public SendMessage commandStart(@ChatId long chatId, @UserId long userId) {
        ChatSource chatSource = TelegramContextHolder.getChatSource();
        if (chatSource != ChatSource.PRIVATE) {
            log.info("Chat ID: {} | User ID: {}", chatId, userId);

            Optional<UserRepresentation> userOpt = userService.findByTelegramIdOpt(userId);
            if (userOpt.isEmpty()) return userListener.register(chatId, userId);
            else return ticketRegistrationFlow.instant_start(chatId);
        }

        return null;
    }

}
