package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.repository.order.LogTicketRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class LogTicketService {

    private final LogTicketRepo repo;

    public LogTicket add(LogTicket history) {
        log.debug("ADD UPDATE HISTORY -- TICKET NO {}", history.getTicket().getNo());
        return repo.save(history);
    }

    public void createOpenToProgressLog(Ticket ticket, TicketAgent agent) {
        repo.save(LogTicket.builder()
                .ticket(ticket)
                .prev(TcStatus.OPEN)
                .curr(TcStatus.PROGRESS)
                .agent(agent)
                .message("work in progress by user " + agent.getUser().getNik() + " (nik)")
                .build());
    }

}
