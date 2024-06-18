package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.general.TicketDashboardForm;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.core.domain.symptom.Issue;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

public interface TicketService {
    Ticket save(Ticket ticket);

    //    @Async
    //    @Transactional
    void updateTicketIssue(Issue issue);

    String generateTicketNo();

    Ticket create(TicketDashboardForm form);

    void delete(String... ticketIds);

    void markDeleted(String... ticketIds);

    void markDeleted(Instant belowDate);

    @Transactional
    void markDeleted(InstantFilter date);

    void markDeleted(TicketCriteria criteria);

    @Transactional
    long markDeleted(Instant from, Instant to);

    @Transactional
    void restore(String... ticketIds);

    File report(TicketCriteria criteria) throws IOException;

    @Transactional
    void resendPending();
}
