package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserSetting;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.repository.credential.UserSettingRepo;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor

@Service
public class NotifierService {

    private final TelegramBotService botService;
    private final UserSettingRepo userSettingRepo;

    public void sendTaken(Ticket ticket, User user) {
        send(ticket.getSenderId(), "tg.ticket.wip",
                ticket.getNo(),
                user.getName());
    }

    public int sendConfirmation(Ticket ticket) {
        try {
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                    .keyboardRow(TC_CONFIRMATION_BTN())
                    .build();

            Locale lang = useLocale(ticket.getSenderId());
            String[] messages = {
                    Translator.tr("tg.ticket.confirm.header", lang, ticket.getNo()),
                    Translator.tr("tg.ticket.confirm.footer", lang, "30 " + Translator.tr("date.minute")),
            };

            SendMessage send = SendMessage.builder()
                    .chatId(ticket.getSenderId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .replyMarkup(markup)
                    .text(TelegramUtil.esc(String.join("\n", messages)))
                    .build();

            Message msg = botService.getClient().execute(send);
            return msg.getMessageId();
        }
        catch (TelegramApiException ex) {
            throw InternalServerException.args(ex, "error.unable.to.notify.user");
        }
    }

    public void send(long telegramId, String codeOrMessage, Object... args) {
        try {
            String message = Translator.tr(codeOrMessage, useLocale(telegramId), args);
            botService.getClient().execute(SendMessage.builder()
                    .chatId(telegramId)
                    .text(TelegramUtil.esc(message))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build());
        }
        catch (TelegramApiException ex) {
            throw InternalServerException.args(ex, "error.unable.to.notify.user");
        }
    }

    private Locale useLocale(long telegramId) {
        return userSettingRepo.findByUserTelegramId(telegramId)
                .map(UserSetting::getLang)
                .orElse(LocaleContextHolder.getLocale());
    }

    private static List<InlineKeyboardButton> TC_CONFIRMATION_BTN() {
        return List.of(
                // Tidak
                InlineKeyboardButton.builder()
                        .text(Translator.tr("text.disagree"))
                        .callbackData(AppConstants.Ticket.CONFIRM_DISAGREE)
                        .build(),
                // Ya
                InlineKeyboardButton.builder()
                        .text(Translator.tr("text.agree"))
                        .callbackData(AppConstants.Ticket.CONFIRM_AGREE)
                        .build()
        );
    }
}
