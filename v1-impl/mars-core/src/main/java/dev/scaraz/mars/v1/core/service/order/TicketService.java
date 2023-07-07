package dev.scaraz.mars.v1.core.service.order;

import dev.scaraz.mars.common.domain.general.TicketDashboardForm;
import dev.scaraz.mars.v1.core.domain.order.Ticket;
import dev.scaraz.mars.v1.core.query.criteria.TicketCriteria;

import java.io.File;
import java.io.IOException;

public interface TicketService {
    Ticket save(Ticket ticket);

    String generateTicketNo();

    Ticket create(TicketDashboardForm form);

    File report(TicketCriteria criteria) throws IOException;
}
