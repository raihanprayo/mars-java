package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.filter.type.EnumFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/api/ticket")
public class TicketResource {

    private final TicketService service;
    private final TicketQueryService queryService;

    private final TicketAgentQueryService agentQueryService;

    @GetMapping
    public ResponseEntity<?> findAll(TicketCriteria criteria, Pageable pageable) {
        Page<Ticket> page = queryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/api/ticket");
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> getInbox(
            TicketCriteria criteria,
            Pageable pageable
    ) {
        TicketAgentCriteria agentCriteria = criteria.getAgents();
        if (agentCriteria == null) {
            agentCriteria = new TicketAgentCriteria();
            criteria.setAgents(agentCriteria);
        }

        agentCriteria.setUserId(new StringFilter().setEq(SecurityUtil.getCurrentUser().getId()));
        agentCriteria.setStatus(new EnumFilter<AgStatus>().setEq(AgStatus.PROGRESS));

        Page<Ticket> page = queryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/api/ticket");
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<?> findById(@PathVariable String ticketId) {
        Ticket ticket = queryService.findById(ticketId);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("/agents/{ticketId}")
    public ResponseEntity<?> findAgents(@PathVariable String ticketId) {
        return new ResponseEntity<>(
                agentQueryService.findByTicketId(ticketId),
                HttpStatus.OK
        );
    }

    @PutMapping("/take/{ticketId}")
    public ResponseEntity<?> takeTicket(
            @PathVariable String ticketId
    ) {
        Ticket ticket = queryService.findByIdOrNo(ticketId);

        if (agentQueryService.hasAgentInProgressByTicketNo(ticketId))
            throw BadRequestException.args("error.ticket.taken");

        return new ResponseEntity<>(
                service.take(ticket),
                HttpStatus.OK
        );
    }

}
