package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserSetting;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.repository.credential.UserSettingRepo;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public void sendRetaken(Ticket ticket, User user) {
        send(ticket.getSenderId(), "tg.ticket.wip.retake",
                ticket.getNo(),
                user.getName());
    }

    public int sendConfirmation(Ticket ticket, int minute) {
        try {
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                    .keyboardRow(CONFIRMATION_QUERY_BTN())
                    .build();

            Locale lang = useLocale(ticket.getSenderId());

            SendMessage send = SendMessage.builder()
                    .chatId(ticket.getSenderId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .replyMarkup(markup)
                    .text(TelegramUtil.esc(Translator.tr("tg.ticket.confirm", lang,
                            ticket.getNo(),
                            minute + " " + Translator.tr("date.minute", lang)
                    )))
                    .build();

            Message msg = botService.getClient().execute(send);
            return msg.getMessageId();
        }
        catch (TelegramApiException ex) {
            throw InternalServerException.args(ex, "error.unable.to.notify.user");
        }
    }

    public int send(long telegramId, String codeOrMessage, Object... args) {
        try {
            log.info("SENDING MESSAGE TO TELEGRAM USER {}", telegramId);

            String message = Translator.tr(codeOrMessage, useLocale(telegramId), args);
            return botService.getClient().execute(SendMessage.builder()
                            .chatId(telegramId)
                            .text(TelegramUtil.esc(message))
                            .parseMode(ParseMode.MARKDOWNV2)
                            .build())
                    .getMessageId();
        }
        catch (TelegramApiException ex) {
            throw InternalServerException.args(ex, "error.unable.to.notify.user");
        }
    }

    public void safeSend(long telegramId, String codeOrMessage, Object... args) {
        try {
            send(telegramId, codeOrMessage, args);
        }
        catch (InternalServerException ex) {
        }
    }

    @Transactional(readOnly = true)
    public Locale useLocale(long telegramId) {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser != null && currentUser.getTg().getId() == telegramId) {
            return currentUser.getSetting().getLang();
        }

        log.debug("GET LOCALE FROM USER SETTING");
        return userSettingRepo.findByUserTgId(telegramId)
                .map(UserSetting::getLang)
                .orElse(Translator.LANG_ID);
    }

    private static List<InlineKeyboardButton> CONFIRMATION_QUERY_BTN() {
        return List.of(
                // Tidak
                InlineKeyboardButton.builder()
                        .text(Translator.tr("text.disagree"))
                        .callbackData(AppConstants.Telegram.CONFIRM_DISAGREE)
                        .build(),
                // Ya
                InlineKeyboardButton.builder()
                        .text(Translator.tr("text.agree"))
                        .callbackData(AppConstants.Telegram.CONFIRM_AGREE)
                        .build()
        );
    }
}
