package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.general.TicketDashboardForm;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.repository.order.TicketRepo;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketServiceImpl implements TicketService {

    private final MarsProperties marsProperties;

    private final TicketRepo repo;

    private final TicketQueryService queryService;

    private final IssueQueryService issueQueryService;

    private final StorageService storageService;

    private final LogTicketService logTicketService;

    @Override
    public Ticket save(Ticket ticket) {
        if (ticket.getNo() == null) ticket.setNo(generateTicketNo());
        return repo.save(ticket);
    }

    @Override
    public String generateTicketNo() {
        LocalDate todayLd = LocalDate.now();

        Instant todayIns = todayLd.atStartOfDay().toInstant(ZoneOffset.of("+07"));

        long total = repo.countByCreatedAtGreaterThanEqual(todayIns) + 1;
        String todayStr = todayLd.format(DateTimeFormatter.ofPattern("yyMMdd"));

        String result = todayStr + StringUtils.leftPad(total + "", 6, "0");
        log.debug("Generating no ticket {}", result);
        return result;
    }

    @Override
    @Transactional
    public Ticket create(TicketDashboardForm form) {
        Issue issue = issueQueryService.findById(form.getIssue())
                .orElseThrow(() -> NotFoundException.entity(Issue.class, "id", form.getIssue()));

        User user = SecurityUtil.getCurrentUser();

        int totalGaul = queryService.countGaul(form.getIssue(), form.getServiceNo());
        Ticket ticket = save(Ticket.builder()
                .witel(Objects.requireNonNullElse(form.getWitel(), marsProperties.getWitel()))
                .sto(form.getSto())
                .issue(issue)
                .incidentNo(form.getIncidentNo())
                .serviceNo(form.getServiceNo())
                .source(TcSource.PRIVATE)
                .senderId(user.getTelegramId())
                .senderName(user.getName())
                .note(form.getNote())
                .gaul(totalGaul)
                .build());

        if (form.getFiles() != null) {
            storageService.addPhotoForTicketDashboardAsync(
                    List.of(form.getFiles()),
                    ticket);
        }

        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .curr(ticket.getStatus())
                .message(String.format(
                        "created ticket with gaul is %s",
                        totalGaul != 0
                ))
                .build());

        return ticket;
    }

}
