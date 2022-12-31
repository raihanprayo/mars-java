package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.general.TicketForm;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.Collection;

public interface TicketBotService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Ticket registerForm(TicketForm form, Collection<PhotoSize> photos);

    void validateForm(TicketForm form) throws TgInvalidFormError;
}
