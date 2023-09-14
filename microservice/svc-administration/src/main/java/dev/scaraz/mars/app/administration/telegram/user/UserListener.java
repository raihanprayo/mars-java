package dev.scaraz.mars.app.administration.telegram.user;

import dev.scaraz.mars.app.administration.service.UserService;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.context.CallbackData;
import dev.scaraz.mars.telegram.annotation.context.ChatId;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Optional;

import static dev.scaraz.mars.app.administration.telegram.ReplyKeyboardConstant.UNREGISTERED_USER;

@TelegramBot
@RequiredArgsConstructor
public class UserListener {

    private final UserService userService;
    private final UserNewRegistrationService userNewRegistrationService;

    @TelegramCommand(
            commands = "/register")
    public SendMessage register(
            @ChatId Long chatId,
            @UserId Long userId,
            Message message
    ) {

        Optional<UserRepresentation> userOpt = userService.findByTelegramIdOpt(userId);
        if (userOpt.isPresent()) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(TelegramUtil.esc(Translator.tr("error.user.registered")))
                    .build();
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
        if (!userNewRegistrationService.isInRegistration(userId))
            return null;

        return userNewRegistrationService.start(userId);
    }

    @TelegramCommand("/reg_end")
    public SendMessage endRegistration(@UserId long userId) {
        if (!userNewRegistrationService.isInRegistration(userId))
            return null;

        userNewRegistrationService.deleteById(userId);
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
                return userNewRegistrationService.start(userId);
            case AppConstants.Telegram.REG_PAIR:
                break;
        }

        return null;
    }

    @TelegramCallbackQuery({AppConstants.Telegram.REG_NEW_AGREE, AppConstants.Telegram.REG_NEW_DISAGREE})
    public SendMessage registrationAgree(@UserId long userId, @CallbackData String data) {
        if (userNewRegistrationService.isInRegistration(userId)) {
            switch (data) {
                case AppConstants.Telegram.REG_NEW_AGREE:
                    return userNewRegistrationService.end(userId);
                case AppConstants.Telegram.REG_NEW_DISAGREE:
                    return userNewRegistrationService.start(userId);
            }
        }

        return null;
    }

}
