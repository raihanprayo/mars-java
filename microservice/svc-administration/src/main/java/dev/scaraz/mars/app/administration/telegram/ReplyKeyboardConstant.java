package dev.scaraz.mars.app.administration.telegram;

import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.utils.AppConstants;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class ReplyKeyboardConstant {
    private ReplyKeyboardConstant() {
    }

    public static final String
            REG_TICKET_NET_AGREE = "REG:TC:NETWORK:" + AppConstants.Telegram.CONFIRM_AGREE,
            REG_TICKET_NET_DISAGREE = "REG:TC:NETWORK:" + AppConstants.Telegram.CONFIRM_DISAGREE;

    public static final String
            REG_TICKET_PARAM_AGREE = "REG:TC:PARAM:" + AppConstants.Telegram.CONFIRM_AGREE,
            REG_TICKET_PARAM_DISAGREE = "REG:TC:PARAM:" + AppConstants.Telegram.CONFIRM_DISAGREE;

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


    public static final List<InlineKeyboardButton> NETWORK_AGGREEMENT = List.of(
            InlineKeyboardButton.builder()
                    .callbackData(REG_TICKET_NET_AGREE)
                    .text("Sudah")
                    .build(),
            InlineKeyboardButton.builder()
                    .callbackData(REG_TICKET_NET_DISAGREE)
                    .text("Belum")
                    .build()
    );


    public static final List<InlineKeyboardButton> PARAM_AGGREEMENT = List.of(
            InlineKeyboardButton.builder()
                    .callbackData(REG_TICKET_PARAM_AGREE)
                    .text("Sudah")
                    .build(),
            InlineKeyboardButton.builder()
                    .callbackData(REG_TICKET_PARAM_DISAGREE)
                    .text("Belum")
                    .build()
    );

}
