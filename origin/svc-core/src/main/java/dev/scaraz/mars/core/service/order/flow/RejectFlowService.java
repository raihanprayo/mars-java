package dev.scaraz.mars.core.service.order.flow;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.security.MarsUserContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RejectFlowService {

    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;

    private final LogTicketService logTicketService;

    public Ticket reject(String ticketIdOrNo, TicketStatusFormDTO form) {
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);
        TicketSummary summary = summaryQueryService.findByIdOrNo(ticket.getId());

        if (summary.isWip() && !summary.getWipBy().equals(MarsUserContext.getId()))
            throw BadRequestException.args("error.ticket.update.stat.agent");

        if (StringUtils.isBlank(form.getNote()))
            throw BadRequestException.args("mohon sertakan alasan ditolaknya tiket");

        TcStatus prevStatus = ticket.getStatus();

        logTicketService.add(LogTicket.builder()
                .prev(prevStatus)
                .curr(ticket.getStatus())
                .message("pengerjaan tiket ditolak")
                .build());
        return service.save(ticket);
    }

}
