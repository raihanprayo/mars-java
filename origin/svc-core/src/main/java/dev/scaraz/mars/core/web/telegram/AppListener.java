package dev.scaraz.mars.core.web.telegram;

import com.google.gson.Gson;
import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.repository.cache.BotRegistrationRepo;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.service.credential.AccountRegistrationBotService;
import dev.scaraz.mars.core.service.order.ConfirmService;
import dev.scaraz.mars.core.service.order.TicketBotService;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import dev.scaraz.mars.telegram.annotation.context.CallbackData;
import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static dev.scaraz.mars.common.utils.AppConstants.Telegram.REPORT_ISSUE;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class AppListener {

    private final TelegramBotService botService;

    private final MarsProperties marsProperties;

    private final AuthService authService;

    private final AccountRegistrationBotService accountRegistrationBotService;
    private final TicketBotService ticketBotService;

    private final ConfirmService confirmService;
    private final BotRegistrationRepo registrationRepo;

    @Lazy
    private final UserListener userListener;

    private final Gson gson = new Gson();

    @TelegramMessage
    public SendMessage generalMessage(
            User user,
            Update update,
            Message message,
            @Text String text,
            @TgAuth() Account marsAccount
    ) {

        log.info("{}", gson.toJson(update));

        if (marsAccount == null) {
            Optional<BotRegistration> registrationOpt = registrationRepo.findById(user.getId());
            if (registrationOpt.isPresent()) {
                BotRegistration registration = registrationOpt.get();
                switch (registration.getState()) {
                    case NAME:
                        return accountRegistrationBotService.answerNameThenAskNik(registration, text.trim());
                    case NIK:
                        return accountRegistrationBotService.answerNikThenAskPhone(registration, text.trim());
                    case PHONE:
                        return accountRegistrationBotService.answerPhoneThenAskWitel(registration, text.trim());
                    case WITEL:
                        try {
                            Witel witel = Witel.valueOf(text.toUpperCase());
                            return accountRegistrationBotService.answerWitelThenAskSubregion(registration, witel);
                        }
                        catch (IllegalArgumentException ex) {
                            return SendMessage.builder()
                                    .chatId(user.getId())
                                    .text("Pilihan witel salah")
                                    .build();
                        }
                    case REGION:
                        return accountRegistrationBotService.answerSubregionThenShowSummary(registration, text.trim());
                    case PAIR_NIK:
                        return accountRegistrationBotService.pairAccountAnsNik(registration, text.trim());
                    case PAIR_WITEL:
                        try {
                            Witel witel = Witel.valueOf(text.toUpperCase());
                            return accountRegistrationBotService.pairAccountAnsWitel(registration, witel);
                        }
                        catch (IllegalArgumentException ex) {
                            return SendMessage.builder()
                                    .chatId(user.getId())
                                    .text("Pilihan witel salah")
                                    .build();
                        }
                }
            }
        }
        else {
            if (message.getReplyToMessage() != null) {
                log.info("REPLY TO MESSAGE STATE -- MESSAGE ID {}", message.getReplyToMessage().getMessageId());

                Message reply = message.getReplyToMessage();
                if (confirmService.existsByIdAndStatus(reply.getMessageId(), TicketConfirm.POST_PENDING_CONFIRMATION)) {
                    ticketBotService.confirmedPostPending(reply.getMessageId(),
                            text,
                            message.getPhoto()
                    );
                }
                else if (confirmService.existsByIdAndStatus(reply.getMessageId(), TicketConfirm.INSTANT_FORM)) {
                    return ticketBotService.instantForm_end(reply.getMessageId(),
                            text,
                            message.getPhoto(),
                            message.getDocument()
                    );
                }
            }
        }

        return null;
    }

    @TelegramCallbackQuery
    public SendMessage onCallbackQuery(
            User user,
            CallbackQuery cq,
            Message message,
            @CallbackData String data,
            @TgAuth Account marsAccount
    ) throws TelegramApiException {
        log.info("{}", gson.toJson(cq));

        int messageId = message.getMessageId();
        if (data.startsWith(REPORT_ISSUE) && confirmService.existsById(messageId)) {
            long issueId = Long.parseLong(data.substring(data.lastIndexOf(":") + 1));
            ticketBotService.instantForm_answerIssue(messageId, issueId);
        }
        else if (data.startsWith("WITEL_") && registrationRepo.existsById(user.getId())) {
            Optional<BotRegistration> registrationOpt = registrationRepo.findById(user.getId());
            if (registrationOpt.isPresent()) {
                Witel witel = Witel.fromCallbackData(data);
                return accountRegistrationBotService.answerWitelThenAskSubregion(registrationOpt.get(), witel);
            }
        }
        return null;
    }

    @TelegramCallbackQuery({AppConstants.Telegram.REG_PAIR, AppConstants.Telegram.REG_NEW, AppConstants.Telegram.REG_IGNORE_WITEL})
    public SendMessage registrationCallback(
            User user,
            Message message,
            @CallbackData String data,
            @TgAuth Account marsAccount
    ) {
        switch (data) {
            case AppConstants.Telegram.REG_PAIR: {
                if (marsAccount != null) return null;
                else if (authService.isUserInApproval(user.getId())) return null;
                return accountRegistrationBotService.pairAccount(user.getId(), user.getUserName());
            }

            case AppConstants.Telegram.REG_NEW: {
                if (marsAccount != null) return null;
                else if (authService.isUserInApproval(user.getId())) return null;
                return accountRegistrationBotService.start(user.getId(), user.getUserName());
            }
            case AppConstants.Telegram.REG_IGNORE_WITEL: {
                if (!registrationRepo.existsById(user.getId())) return null;
                BotRegistration registration = registrationRepo.findById(user.getId())
                        .orElseThrow();

                RegisterState state = registration.getState();
                if (state == RegisterState.WITEL)
                    return accountRegistrationBotService.answerWitelThenAskSubregion(registration, marsProperties.getWitel());

                throw new IllegalStateException("Invalid registration state");
            }
        }

        return null;
    }

    @TelegramCallbackQuery({AppConstants.Telegram.CONFIRM_AGREE, AppConstants.Telegram.CONFIRM_DISAGREE})
    public SendMessage agreeDisagreeCallback(
            Message message,
            @CallbackData String data,
            @TgAuth Account marsAccount
    ) throws TelegramApiException {
        // Disini tiket bisa jadi kondisi status mau pending atau close
        boolean agree = AppConstants.Telegram.CONFIRM_AGREE.equals(data);
        log.info("CALLBACK QUERY agreeDisagreeCallback");

        int messageId = message.getMessageId();
        if (confirmService.existsByIdAndStatus(messageId, TicketConfirm.CLOSED)) {
            log.info("TICKET CLOSE CONFIRMATION REPLY -- MESSAGE ID={} CLOSE={}", messageId, agree);
            ticketBotService.confirmedClose(message.getMessageId(), agree, null, null);
        }
        else if (confirmService.existsByIdAndStatus(messageId, TicketConfirm.PENDING)) {
            log.info("TICKET PENDING CONFIRMATION REPLY -- MESSAGE ID={} PENDING={}", messageId, agree);
            ticketBotService.confirmedPending(message.getMessageId(), agree);
        }
        else if (confirmService.existsByIdAndStatus(messageId, TicketConfirm.POST_PENDING)) {
            log.info("TICKET {} CONFIRMATION REPLY -- MESSAGE ID={} PENDING={}", TicketConfirm.POST_PENDING, messageId, agree);
            ticketBotService.confirmedPostPending(messageId, null, null);
        }
        else if (confirmService.existsByIdAndStatus(messageId, TicketConfirm.POST_PENDING_CONFIRMATION)) {
            log.info("TICKET {} CONFIRMATION REPLY -- MESSAGE ID={} CLOSE={}", TicketConfirm.POST_PENDING_CONFIRMATION, messageId, agree);
            ticketBotService.confirmedPostPendingConfirmation(messageId, agree, null, null);
        }
        else if (confirmService.existsByIdAndStatus(messageId, TicketConfirm.INSTANT_NETWORK)) {
            log.info("{} CONFIRMATION REPLY -- MESSAGE ID={} AGREE={}", TicketConfirm.INSTANT_NETWORK, messageId, agree);
            return ticketBotService.instantForm_answerNetwork(messageId, agree);
        }
        else if (confirmService.existsByIdAndStatus(messageId, TicketConfirm.INSTANT_PARAM)) {
            log.info("{} CONFIRMATION REPLY -- MESSAGE ID={} AGREE={}", TicketConfirm.INSTANT_NETWORK, messageId, agree);
            return ticketBotService.instantForm_answerParamRequirement(messageId, agree);
        }

        return null;
    }

    @TelegramCommand(commands = "/start")
    public SendMessage start(
            @UserId Long tgUserId,
            Message message
    ) throws TelegramApiException {
        if (TelegramContextHolder.getChatSource() == ChatSource.PRIVATE) {
            Optional<Account> account = authService.optionalAuthenticationFromBot(TelegramContextHolder.getUserId());
            if (account.isPresent())
                return ticketBotService.instantForm_start(tgUserId);
            else
                return userListener.register(tgUserId, message);
        }

        return null;
    }

    @TelegramCommand(commands = "/help", isHelp = true)
    public SendMessage help(@UserId long telegramId, @Text String arg) {
        if (StringUtils.isNoneBlank(arg)) {
            return detailedHelp(arg)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .chatId(telegramId)
                    .build();
        }

        return SendMessage.builder()
                .chatId(telegramId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "Command yang tersedia:",
                        "*/start*",
                        "Instan Form Lapor",
                        "",
                        "*/reg /register*",
                        "Untuk melakukan registrasi user",
                        "",
                        "*/report /lapor*",
                        "Form lapor manual",
                        "",
                        "*/take /sayaambil* _<no-tiket>_",
                        "Untuk mengambil tiket, dimana argument _<no-tiket>_ adalah no tiket yang tersedia",
                        "",
                        "*/confirm* _<no-tiket>_",
                        "Untuk melakukan konfirmasi tiket yang sedang dalam keadaan pending",
                        "",
                        "*/tiket* _<no-tiket>_",
                        "Untuk melihat info/summary tiket",
                        "",
                        "*/help* _[cmd]_",
                        "List command yang tersedia, Dimana argument _[cmd]_ (optional) nama command yang tersedia untuk detail penjelasan (tanpa forward slash).",
                        "",
                        "",
                        "*Note*:",
                        "_<text>_ - required argument",
                        "_[text]_ - optional argument"
                ))
                .build();
    }


    private SendMessage.SendMessageBuilder detailedHelp(String arg) {
        if (isEqual(arg, "report", "lapor")) {
            return SendMessage.builder()
                    .text(TelegramUtil.esc(
                            "/report atau /lapor",
                            "Untuk menginput tiket/order dengan menggunakan format yang tersedia",
                            "",
                            TelegramUtil.REPORT_FORMAT()
                    ));
        }

        return SendMessage.builder()
                .text("_No detailed descriptions_");
    }

    private boolean isEqual(String target, String... predicate) {
        for (String p : predicate) {
            boolean b = p.equalsIgnoreCase(target);
            if (b) return true;
        }
        return false;
    }
}
