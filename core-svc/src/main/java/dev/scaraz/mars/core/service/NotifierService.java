package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserApproval;
import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.repository.credential.UserSettingRepo;
import dev.scaraz.mars.core.repository.order.SolutionRepo;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
public class NotifierService {

    private final TelegramBotService botService;
    private final UserSettingRepo userSettingRepo;

    @Lazy
    private final AppConfigService appConfigService;

    @Lazy
    private final SolutionRepo solutionRepo;

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

    public int sendCloseConfirmation(Ticket ticket, int minute, TicketStatusFormDTO form) {
        try {
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                    .keyboardRow(List.of(BTN_AGREE))
                    .build();

            String expireMinute = minute + " " + Translator.tr("date.minute");
            Optional<TicketStatusFormDTO> optForm = Optional.ofNullable(form);
            SendMessage send = SendMessage.builder()
                    .chatId(ticket.getSenderId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .replyMarkup(markup)
                    .text(TelegramUtil.esc(
                            String.format("Tiket *%s*:", ticket.getNo()),
                            "Telah selesai dikerjakan.",
                            "Harap konfirmasi bahwa masalah telah terselesaikan.",
                            "",
                            "Actual Solution: " + optForm
                                    .map(TicketStatusFormDTO::getSolution)
                                    .flatMap(solutionRepo::findById)
                                    .map(Solution::getName)
                                    .orElse("-")
                            ,
                            "Worklog: " + optForm
                                    .map(TicketStatusFormDTO::getNote)
                                    .orElse("-"),
                            "",
                            "_Balas pesan ini dengan mengreply command /reopen dan tambahkan deskripsi jika diperlukan, atau menekan tombol yang disediakan.\n",
                            "Jika dalam " + expireMinute + " tidak ada respon, tiket akan close secara otomatis_"
                    ))
                    .build();

            Message msg = botService.getClient().execute(send);
            return msg.getMessageId();
        }
        catch (TelegramApiException ex) {
            throw InternalServerException.args(ex, "error.unable.to.notify.user");
        }
    }

    public int sendPendingConfirmation(Ticket ticket, int minute, TicketStatusFormDTO form) {
        try {
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                    .keyboardRow(List.of(BTN_PENDING, BTN_PENDING_CLOSE))
                    .build();

            String replyDuration = minute + " " + Translator.tr("date.minute");

            int pendingMinute = appConfigService.getPostPending_int()
                    .getAsNumber()
                    .intValue();
            String pendingDuration = String.format("%s %s",
                    pendingMinute,
                    Translator.tr("date.minute")
            );

            Optional<TicketStatusFormDTO> optForm = Optional.ofNullable(form);
            SendMessage send = SendMessage.builder()
                    .chatId(ticket.getSenderId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .replyMarkup(markup)
                    .text(TelegramUtil.esc(Translator.tr(
                            "tg.ticket.pending.confirm",
                            ticket.getNo(),
                            pendingDuration,
                            replyDuration,
                            optForm.map(TicketStatusFormDTO::getSolution)
                                    .flatMap(solutionRepo::findById)
                                    .map(Solution::getName)
                                    .orElse("-"),
                            optForm.map(TicketStatusFormDTO::getNote)
                                    .orElse("-")
                    )))
                    .build();

            Message msg = botService.getClient().execute(send);
            return msg.getMessageId();
        }
        catch (TelegramApiException ex) {
            throw InternalServerException.args(ex, "error.unable.to.notify.user");
        }
    }

    public int sendPostPendingConfirmation(Ticket ticket, int minute) {
        try {
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                    .keyboardRow(CONFIRMATION_POST_PENDING)
                    .build();

            String replyDuration = minute + " " + Translator.tr("date.minute");

            SendMessage send = SendMessage.builder()
                    .chatId(ticket.getSenderId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .replyMarkup(markup)
                    .text(TelegramUtil.esc(Translator.tr(
                            "tg.ticket.post.pending.confirm",
                            ticket.getNo(),
                            replyDuration
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

            String message = Translator.tr(codeOrMessage, args);
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

    public int sendRaw(long telegramId, String... messages) {
        try {
            log.info("SENDING MESSAGE TO TELEGRAM USER {}", telegramId);
            return botService.getClient().execute(SendMessage.builder()
                            .chatId(telegramId)
                            .text(TelegramUtil.esc(messages))
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

//    @Transactional(readOnly = true)
//    public Locale useLocale(long telegramId) {
//        User currentUser = SecurityUtil.getCurrentUser();
//        if (currentUser != null && currentUser.getTg().getId() == telegramId) {
//            return currentUser.getSetting().getLang();
//        }
//
//        log.debug("GET LOCALE FROM USER SETTING");
//        return userSettingRepo.findByUserTgId(telegramId)
//                .map(UserSetting::getLang)
//                .orElse(Translator.LANG_ID);
//    }


    public static final InlineKeyboardButton
            BTN_AGREE = InlineKeyboardButton.builder()
            .text(Translator.tr("text.agree"))
            .callbackData(AppConstants.Telegram.CONFIRM_AGREE)
            .build(),
            BTN_DISAGREE = InlineKeyboardButton.builder()
                    .text(Translator.tr("text.disagree"))
                    .callbackData(AppConstants.Telegram.CONFIRM_DISAGREE)
                    .build(),
            BTN_PENDING = InlineKeyboardButton.builder()
                    .text(Translator.tr("text.btn.pending"))
                    .callbackData(AppConstants.Telegram.CONFIRM_AGREE)
                    .build(),
            BTN_PENDING_CLOSE = InlineKeyboardButton.builder()
                    .text(Translator.tr("text.bnt.close"))
                    .callbackData(AppConstants.Telegram.CONFIRM_DISAGREE)
                    .build();

    public static final List<InlineKeyboardButton> CONFIRMATION_POST_PENDING = List.of(
            InlineKeyboardButton.builder()
                    .text(Translator.tr("text.btn.done"))
                    .callbackData(AppConstants.Telegram.CONFIRM_AGREE)
                    .build()
    );

    public static final List<InlineKeyboardButton> UNREGISTERED_USER = List.of(
            InlineKeyboardButton.builder()
                    .callbackData(AppConstants.Telegram.REG_PAIR)
                    .text(Translator.tr("text.btn.pair"))
                    .build(),
            InlineKeyboardButton.builder()
                    .callbackData(AppConstants.Telegram.REG_NEW)
                    .text(Translator.tr("text.btn.regist"))
                    .build()
    );

    public static SendMessage WAITING_APPROVAL(long telegramId, UserApproval approval) {
        return SendMessage.builder()
                .chatId(telegramId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "Mohon menunggu registrasi disetujui",
                        "",
                        "NO: " + approval.getNo(),
                        "Tgl Dibuat: " + approval.getCreatedAt()
                                .atZone(ZoneId.of("Asia/Jakarta"))
                                .toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                ))
                .build();
    }

    public static InlineKeyboardButton BTN_AGREE_CUSTOM(String text) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(AppConstants.Telegram.CONFIRM_AGREE)
                .build();
    }

    public static InlineKeyboardButton BTN_DIAGREE_CUSTOM(String text) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(AppConstants.Telegram.CONFIRM_DISAGREE)
                .build();
    }
}
