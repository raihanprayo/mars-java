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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                                .text("Pilihan witel salah")
                                .build();
                    }
                case REGION:
                    return userBotService.answerSubregionThenEnd(registration, text.trim());
            }
        }

        return null;
    }

    @TelegramCallbackQuery
    public SendMessage onCallbackQuery(CallbackQuery cq, @UserId long telegramId) {
        log.info("{}", gson.toJson(cq));

        Message message = cq.getMessage();
        if (confirmRepo.existsById(Long.valueOf(message.getMessageId()))) {
            boolean answer = AppConstants.Telegram.CONFIRM_AGREE.equals(cq.getData());
            log.info("TICKET CONFIRMATION REPLY -- MESSAGE ID={} CLOSE={}", message.getMessageId(), answer);

            ticketBotService.confirmedClose(
                    message.getMessageId(),
                    answer,
                    ""
            );
        }
        else if (registrationRepo.existsById(telegramId)) {
            BotRegistration registration = registrationRepo.findById(telegramId)
                    .orElseThrow();

            RegisterState state = registration.getState();
            if (state == RegisterState.WITEL)
                userBotService.answerWitelThenAskSubregion(registration, marsProperties.getWitel());
            else throw new IllegalStateException("Invalid registration state");
        }

        return null;
    }

}
