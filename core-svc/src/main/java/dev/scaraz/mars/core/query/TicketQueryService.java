package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketQueryService {
    List<Ticket> findAll();

    Page<Ticket> findAll(Pageable pageable);

    List<Ticket> findAll(TicketCriteria criteria);

    Page<Ticket> findAll(TicketCriteria criteria, Pageable pageable);

    int countByServiceNo(String serviceNo);
}
