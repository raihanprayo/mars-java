package dev.scaraz.mars.app.administration.telegram;

import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.utils.AppConstants;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class ReplyKeyboardConstant {
    private ReplyKeyboardConstant() {
    }

    public static final List<InlineKeyboardButton> UNREGISTERED_USER = List.of(
            InlineKeyboardButton.builder()
                    .callbackData(AppConstants.Telegram.REG_PAIR)
                    .text(Translator.tr("Account Pairing"))
                    .build(),
            InlineKeyboardButton.builder()
                    .callbackData(AppConstants.Telegram.REG_NEW)
                    .text(Translator.tr("Registrasi"))
                    .build()
    );
}
