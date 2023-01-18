package dev.scaraz.mars.core.web.telegram;

import com.google.gson.Gson;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.repository.cache.StatusConfirmRepo;
import dev.scaraz.mars.core.service.order.TicketBotService;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class AppListener {

    private final TicketBotService ticketBotService;
    private final StatusConfirmRepo confirmRepo;

    private final Gson gson = new Gson();

    @TelegramMessage
    public void generalMessage(Update update) {
        log.info("{}", gson.toJson(update));
    }

    @TelegramCallbackQuery
    public void onCallbackQuery(CallbackQuery cq) {
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
    }

}
