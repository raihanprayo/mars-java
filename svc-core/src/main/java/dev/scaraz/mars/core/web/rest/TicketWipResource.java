package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.general.TicketDashboardForm;
import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.service.order.flow.CloseFlowService;
import dev.scaraz.mars.core.service.order.flow.DispatchFlowService;
import dev.scaraz.mars.core.service.order.flow.PendingFlowService;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/ticket/wip")
public class TicketWipResource {

    private final TicketService service;

    private final TicketFlowService flowService;
    private final CloseFlowService closeFlowService;
    private final PendingFlowService pendingFlowService;
    private final DispatchFlowService dispatchFlowService;

    private final TicketSummaryQueryService summaryQueryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@ModelAttribute @Valid TicketDashboardForm form) {
        Ticket ticket = service.create(form);
        return new ResponseEntity<>(
                summaryQueryService.findByIdOrNo(ticket.getId()),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/take/{ticketIdOrNo}")
    public ResponseEntity<?> takeTicket(
            @PathVariable String ticketIdOrNo
    ) {
        Account account = SecurityUtil.getCurrentUser();
        if (account.getTg().getId() == null)
            throw BadRequestException.args("Akun anda belum terintegrasi dengan akun telegram, silahkan melakukan \"Account Pairing\"");

        flowService.take(ticketIdOrNo);
        return new ResponseEntity<>(
                summaryQueryService.findByIdOrNo(ticketIdOrNo),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/close/{ticketIdOrNo}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> closeTicket(
            @PathVariable String ticketIdOrNo,
            @Valid @ModelAttribute TicketStatusFormDTO form
    ) {
        form.setStatus(TcStatus.CLOSED);
        closeFlowService.close(ticketIdOrNo, form);
        return new ResponseEntity<>(
                summaryQueryService.findByIdOrNo(ticketIdOrNo),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/dispatch/{idOrNo}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> dispatchTicket(
            @PathVariable("idOrNo") String ticketIdOrNo,
            @Valid @ModelAttribute TicketStatusFormDTO form
    ) {
        form.setStatus(TcStatus.DISPATCH);
        dispatchFlowService.dispatch(ticketIdOrNo, form);
        return new ResponseEntity<>(
                summaryQueryService.findByIdOrNo(ticketIdOrNo),
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/pending/{idOrNo}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> pendingTicket(
            @PathVariable("idOrNo") String ticketIdOrNo,
            @Valid @ModelAttribute TicketStatusFormDTO form
    ) {
        form.setStatus(TcStatus.PENDING);
        pendingFlowService.pending(ticketIdOrNo, form);
        return new ResponseEntity<>(
                summaryQueryService.findByIdOrNo(ticketIdOrNo),
                HttpStatus.OK
        );
    }
}
