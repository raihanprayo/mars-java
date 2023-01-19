package dev.scaraz.mars.core.web.telegram;

import com.google.gson.Gson;
import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.repository.cache.BotRegistrationRepo;
import dev.scaraz.mars.core.repository.cache.StatusConfirmRepo;
import dev.scaraz.mars.core.service.credential.UserBotService;
import dev.scaraz.mars.core.service.order.TicketBotService;
import dev.scaraz.mars.telegram.annotation.*;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class AppListener {

    private final MarsProperties marsProperties;

    private final UserBotService userBotService;
    private final TicketBotService ticketBotService;

    private final StatusConfirmRepo confirmRepo;
    private final BotRegistrationRepo registrationRepo;

    private final Gson gson = new Gson();

    @TelegramMessage
    public SendMessage generalMessage(User user, Update update, @Text String text) {
        log.info("{}", gson.toJson(update));

        if (registrationRepo.existsById(user.getId())) {
            BotRegistration registration = registrationRepo.findById(user.getId())
                    .orElseThrow();

            switch (registration.getState()) {
                case NAME:
                    return userBotService.answerNameThenAskNik(registration, text.trim());
                case NIK:
                    return userBotService.answerNikThenAskPhone(registration, text.trim());
                case PHONE:
                    return userBotService.answerPhoneThenAskWitel(registration, text.trim());
                case WITEL:
                    try {
                        Witel witel = Witel.valueOf(text.toUpperCase());
                        return userBotService.answerWitelThenAskSubregion(registration, witel);
                    }
                    catch (IllegalArgumentException ex) {
                        return SendMessage.builder()
                                .chatId(user.getId())
                                .text("Pilihan witel salah")
                                .build();
                    }
                case REGION:
                    return userBotService.answerSubregionThenEnd(registration, text.trim());
                case PAIR_NIK:
                    return userBotService.pairAccountAnsNik(registration, text.trim());
                case PAIR_WITEL:
                    try {
                        Witel witel = Witel.valueOf(text.toUpperCase());
                        return userBotService.pairAccountAnsWitel(registration, witel);
                    }
                    catch (IllegalArgumentException ex) {
                        return SendMessage.builder()
                                .chatId(user.getId())
                                .text("Pilihan witel salah")
                                .build();
                    }
            }
        }

        return null;
    }

    @TelegramCallbackQuery
    public SendMessage onCallbackQuery(CallbackQuery cq, User user) {
        log.info("{}", gson.toJson(cq));

        Message message = cq.getMessage();
        String queryData = cq.getData();

        switch (queryData) {
            case AppConstants.Telegram.CONFIRM_AGREE:
            case AppConstants.Telegram.CONFIRM_DISAGREE:
                if (!confirmRepo.existsById(Long.valueOf(message.getMessageId()))) return null;
                boolean answer = AppConstants.Telegram.CONFIRM_AGREE.equals(cq.getData());
                log.info("TICKET CONFIRMATION REPLY -- MESSAGE ID={} CLOSE={}", message.getMessageId(), answer);

                ticketBotService.confirmedClose(
                        message.getMessageId(),
                        answer,
                        ""
                );
                break;
            case AppConstants.Telegram.REG_PAIR:
                return userBotService.pairAccount(user.getId(), user.getUserName());
            case AppConstants.Telegram.REG_NEW:
                return userBotService.start(user.getId(), user.getUserName());
            case AppConstants.Telegram.REG_IGNORE_WITEL:
                if (!registrationRepo.existsById(user.getId())) return null;
                BotRegistration registration = registrationRepo.findById(user.getId())
                        .orElseThrow();

                RegisterState state = registration.getState();
                if (state == RegisterState.WITEL)
                    return userBotService.answerWitelThenAskSubregion(registration, marsProperties.getWitel());

                throw new IllegalStateException("Invalid registration state");
        }
        return null;
    }

    @TelegramCommand(commands = "/start")
    public void start(User user) {
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
                        "/report /lapor",
                        "Untuk menginput tiket/order",
                        "",
                        "/take /sayaambil <no-tiket>",
                        "Untuk mengambil tiket, dimana argument <no-tiket> adalah no tiket yang tersedia",
                        "",
                        "/reg /register",
                        "Untuk melakukan registrasi user",
                        "",
                        "/help [cmd]",
                        "List command yang tersedia, Dimana argument [cmd] nama command yang tersedia untuk detail penjelasan (tanpa slash)."
//                        "/help",
//                        "List command yang tersedia"
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
