package dev.scaraz.mars.app.administration.telegram.user;

import dev.scaraz.mars.app.administration.domain.cache.FormRegistrationCache;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.context.CallbackData;
import dev.scaraz.mars.telegram.annotation.context.ChatId;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.config.TelegramHandlerMapper;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static dev.scaraz.mars.app.administration.telegram.ReplyKeyboardConstant.UNREGISTERED_USER;

@Slf4j
@TelegramBot
@RequiredArgsConstructor
public class UserListener {

    private final UserService userService;
    private final UserNewRegistrationFlow userNewRegistrationFlow;

    @Lazy
    private final TelegramHandlerMapper telegramHandlerMapper;

    @TelegramCommand(
            commands = "/register")
    public SendMessage register(
            @ChatId long chatId,
            @UserId long userId
    ) {
        ChatSource chatSource = TelegramContextHolder.getChatSource();
        if (chatSource != ChatSource.PRIVATE)
            throw new BadRequestException("command ini hanya bisa dilakukan melalui private chat");

        Optional<UserRepresentation> userOpt = userService.findByTelegramIdOpt(userId);
        if (userOpt.isPresent()) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(TelegramUtil.esc(Translator.tr("error.user.registered")))
                    .build();
        }
        else if (userNewRegistrationFlow.isInRegistration(userId)) {
            FormRegistrationCache cache = userNewRegistrationFlow.get(userId);
            SendMessage prompt = userNewRegistrationFlow.getPrompt(cache, cache.getState());
            prompt.setParseMode(ParseMode.MARKDOWNV2);
            prompt.setText(String.join("\n",
                    "*Silahkan melanjutkan dari pertanyaan sebelumnya.*",
                    "",
                    prompt.getText()
            ));
            return prompt;
        }

        return SendMessage.builder()
                .chatId(chatId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "User tidak dikenali oleh *MARS*. Harap melakukan registrasi terlebih dahulu, ",
                        "atau integrasi akun yang ada dengan akun telegram anda"
                ))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(UNREGISTERED_USER.get(1)))
                        .build())
                .build();
    }

    @TelegramCommand("/reg_reset")
    public SendMessage resetRegistration(@UserId long userId) {
        if (!userNewRegistrationFlow.isInRegistration(userId))
            return null;

        return userNewRegistrationFlow.start(userId);
    }

    @TelegramCommand("/reg_end")
    public SendMessage endRegistration(@UserId long userId) {
        if (!userNewRegistrationFlow.isInRegistration(userId))
            return null;

        userNewRegistrationFlow.deleteById(userId);
        return SendMessage.builder()
                .chatId(userId)
                .text("Proses registrasi dihentikan!")
                .build();
    }

    @TelegramCallbackQuery({AppConstants.Telegram.REG_NEW, AppConstants.Telegram.REG_PAIR})
    public SendMessage registrationCallback(
            @UserId long userId,
            @CallbackData String data
    ) {
        switch (data) {
            case AppConstants.Telegram.REG_NEW:
                return userNewRegistrationFlow.start(userId);
            case AppConstants.Telegram.REG_PAIR:
                break;
        }

        return null;
    }

    @TelegramCallbackQuery({AppConstants.Telegram.REG_NEW_AGREE, AppConstants.Telegram.REG_NEW_DISAGREE})
    public SendMessage registrationAgree(@UserId long userId, @CallbackData String data) {
        if (userNewRegistrationFlow.isInRegistration(userId)) {
            switch (data) {
                case AppConstants.Telegram.REG_NEW_AGREE:
                    return userNewRegistrationFlow.end(userId);
                case AppConstants.Telegram.REG_NEW_DISAGREE:
                    return userNewRegistrationFlow.start(userId);
            }
        }

        return null;
    }

    public SendMessage registrationAnswerWitel(
            @UserId long userId,
            @CallbackData String data
    ) {
        FormRegistrationCache cache = userNewRegistrationFlow.get(userId);
        userNewRegistrationFlow.answer(cache, data);

        if (cache.getWitel() == Witel.ROC)
            return userNewRegistrationFlow.summary(cache);

        cache.setState(RegisterState.REGION);
        userNewRegistrationFlow.save(cache);
        return userNewRegistrationFlow.getPrompt(cache, RegisterState.REGION);
    }

    @PostConstruct
    private void initWitelQuery() {
        telegramHandlerMapper.addHandlers(OptionalLong.empty(), t -> {
            for (Witel witel : Witel.values()) {
                log.debug("Add Witel Callback Query: {}", witel);
                try {
                    t.getCallbackQueryList().put(witel.callbackData(), TelegramHandler.builder()
                            .bean(this)
                            .method(getClass().getDeclaredMethod("registrationAnswerWitel", Long.TYPE, String.class))
                            .build());
                }
                catch (NoSuchMethodException e) {

                }
            }
        });
    }
}
