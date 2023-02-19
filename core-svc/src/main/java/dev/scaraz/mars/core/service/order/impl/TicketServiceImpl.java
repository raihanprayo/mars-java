package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.general.TicketDashboardForm;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dev.scaraz.mars.common.utils.AppConstants.TICKET_CSV_HEADER;
import static dev.scaraz.mars.common.utils.AppConstants.ZONE_LOCAL;

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
                .senderId(user.getTg().getId())
                .senderName(user.getName())
                .note(form.getNote())
                .gaul(totalGaul)
                .build());

        storageService.addDashboardAssets(ticket, form.getFilesCollection());

        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .curr(ticket.getStatus())
                .message("Order Created")
                .build());

        return ticket;
    }

    @Override
    @Transactional(readOnly = true)
    public File report(TicketCriteria criteria) throws IOException {
        List<Ticket> tickets = queryService.findAll(criteria);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");

        List<List<String>> rows = tickets.stream()
                .map(ticket -> {
                    List<String> row = new ArrayList<>();
                    row.add(ticket.getNo());
                    row.add(ticket.getWitel().name());
                    row.add(nonNull(ticket.getSto(), "-"));
                    row.add(nonNull(ticket.getIncidentNo(), "-"));
                    row.add(nonNull(ticket.getServiceNo(), "-"));
                    row.add(ticket.getSource().name());
                    row.add(ticket.getSenderName());
                    row.add(ticket.isGaul() ? "Y" : "N");
                    row.add(ticket.getIssue().getName());
                    row.add(ticket.getIssue().getProduct().name());

                    row.add(ticket.getCreatedAt()
                            .atZone(ZONE_LOCAL)
                            .format(formatter));
                    row.add(Optional.ofNullable(ticket.getUpdatedAt())
                            .map(ins -> ins.atZone(ZONE_LOCAL)
                                    .toLocalDateTime()
                                    .format(formatter))
                            .orElse("-"));
                    return row;
                })
                .collect(Collectors.toList());

        File tmpFile = storageService.createFile(DirectoryAlias.TMP, "report", UUID.randomUUID() + ".csv");
        try (FileWriter writer = new FileWriter(tmpFile)) {
            writer.write(String.join(";", TICKET_CSV_HEADER) + "\n");

            for (List<String> row : rows) {
                writer.write(String.join(";", row) + "\n");
            }

            return tmpFile;
        }
    }

    private <T> T nonNull(T a, T defaults) {
        return Objects.requireNonNullElse(a, defaults);
    }

}
