package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface TicketBotService {

    SendMessage info(String ticketNo);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Ticket registerForm(TicketBotForm form, Collection<PhotoSize> photos);

    @Transactional
    Ticket registerForm(TicketBotForm form,
                        @Nullable Collection<PhotoSize> photos,
                        @Nullable Document document
    );

    Ticket take(String ticketNo);

    @Transactional
    void confirmedClose(
            long messageId,
            boolean closeTicket,
            @Nullable String note,
            @Nullable List<PhotoSize> photos);

    void confirmedPending(long messageId, boolean pendingTicket);

    void confirmedPostPending(long messageId, String text, @Nullable Collection<PhotoSize> photos);
    void confirmedPostPendingConfirmation(long messageId, boolean agree, String text, @Nullable Collection<PhotoSize> photos);


    void endPendingEarly(long messageId, String ticketNo);

    void validateForm(TicketBotForm form) throws TgInvalidFormError;

    // Instant Form
    SendMessage instantForm_start(Long chatId) throws TelegramApiException;

    // Instant Form
    void instantForm_answerIssue(long userId, long issueId) throws TelegramApiException;

    SendMessage instantForm_answerNetwork(long messageId, boolean agree) throws TelegramApiException;

    @Transactional
    SendMessage instantForm_answerParamRequirement(long messageId, boolean agree) throws TelegramApiException;

    @Transactional
    SendMessage instantForm_end(long messageId,
                                String text,
                                @Nullable Collection<PhotoSize> captures,
                                @Nullable Document document);
}
