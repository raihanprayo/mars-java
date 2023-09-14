package dev.scaraz.mars.app.administration.telegram;

import dev.scaraz.mars.app.administration.domain.cache.UserRegistrationCache;
import dev.scaraz.mars.app.administration.telegram.user.UserNewRegistrationService;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@TelegramBot
@RequiredArgsConstructor
public class AppListener {

    private final UserNewRegistrationService userNewRegistrationService;

    @TelegramMessage
    public SendMessage onMessage(@UserId long userId, @Text String text) {
        if (userNewRegistrationService.isInRegistration(userId)) {
            UserRegistrationCache cache = userNewRegistrationService.get(userId);
            userNewRegistrationService.answer(cache, text);

            switch (cache.getState()) {
                case NAME:
                    cache.setState(RegisterState.NIK);
                    userNewRegistrationService.save(cache);
                    return userNewRegistrationService.getPrompt(cache, RegisterState.NIK);
                case NIK:
                    cache.setState(RegisterState.PHONE);
                    userNewRegistrationService.save(cache);
                    return userNewRegistrationService.getPrompt(cache, RegisterState.PHONE);
                case PHONE:
                    cache.setState(RegisterState.WITEL);
                    userNewRegistrationService.save(cache);
                    return userNewRegistrationService.getPrompt(cache, RegisterState.WITEL);
                case WITEL:
                    if (cache.getWitel() == Witel.ROC)
                        return userNewRegistrationService.summary(cache);
                    else {
                        cache.setState(RegisterState.REGION);
                        userNewRegistrationService.save(cache);
                        return userNewRegistrationService.getPrompt(cache, RegisterState.REGION);
                    }
                case REGION:
                    return userNewRegistrationService.summary(cache);
            }
        }

        return null;
    }

}
