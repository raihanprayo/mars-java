package dev.scaraz.mars.app.administration.telegram;

import dev.scaraz.mars.app.administration.domain.cache.UserRegistrationCache;
import dev.scaraz.mars.app.administration.telegram.user.UserNewRegistrationFlow;
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

    private final UserNewRegistrationFlow userNewRegistrationFlow;

    @TelegramMessage
    public SendMessage onMessage(@UserId long userId, @Text String text) {
        if (userNewRegistrationFlow.isInRegistration(userId)) {
            UserRegistrationCache cache = userNewRegistrationFlow.get(userId);
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
                case WITEL:
                    if (cache.getWitel() == Witel.ROC)
                        return userNewRegistrationFlow.summary(cache);
                    else {
                        cache.setState(RegisterState.REGION);
                        userNewRegistrationFlow.save(cache);
                        return userNewRegistrationFlow.getPrompt(cache, RegisterState.REGION);
                    }
                case REGION:
                    return userNewRegistrationFlow.summary(cache);
            }
        }

        return null;
    }

}
