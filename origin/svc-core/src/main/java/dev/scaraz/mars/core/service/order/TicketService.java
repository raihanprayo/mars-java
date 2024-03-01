package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.general.TicketDashboardForm;
import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

public interface TicketService {
    Ticket save(Ticket ticket);

    String generateTicketNo();

    Ticket create(TicketDashboardForm form);

    File report(TicketCriteria criteria) throws IOException;

    @Transactional
    void resendPending();
}
