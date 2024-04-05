package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.general.TicketDashboardForm;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.TcIssue;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.*;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.spec.TicketSpecBuilder;
import dev.scaraz.mars.core.repository.db.order.TicketConfirmRepo;
import dev.scaraz.mars.core.repository.db.order.TicketRepo;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.service.order.flow.PendingFlowService;
import dev.scaraz.mars.security.MarsUserContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
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

    private final ApplicationContext applicationContext;

    private final MarsProperties marsProperties;
    private final AccountQueryService accountQueryService;

    private final TicketRepo repo;
    private final TicketSpecBuilder specBuilder;
    private final TicketConfirmRepo ticketConfirmRepo;

    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;

    private final AgentService agentService;
    private final AgentWorkspaceQueryService agentWorkspaceQueryService;
    private final AgentWorklogQueryService agentWorklogQueryService;
    private final IssueQueryService issueQueryService;

    private final StorageService storageService;

    private final LogTicketService logTicketService;

    private final EntityManager entityManager;

    @Override
    public Ticket save(Ticket ticket) {
        if (ticket.getNo() == null) ticket.setNo(generateTicketNo());
        return repo.save(ticket);
    }

    @Async
    @Transactional
    @Override
    public void updateTicketIssue(Issue issue) {
        repo.updateIssueByIssueId(
                issue.getId(),
                issue.getName(),
                issue.getDescription(),
                issue.getProduct(),
                issue.getScore()
        );
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

        Account account = accountQueryService.findByCurrentAccess();

        int totalGaul = queryService.countGaul(form.getIssue(), form.getServiceNo());
        Ticket ticket = save(Ticket.builder()
                .witel(Objects.requireNonNullElse(form.getWitel(), marsProperties.getWitel()))
                .sto(form.getSto())
                .issue(TcIssue.from(issue))
                .incidentNo(form.getIncidentNo())
                .serviceNo(form.getServiceNo())
                .source(TcSource.PRIVATE)
                .senderId(account.getTg().getId())
                .senderName(account.getName())
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
    @Transactional
    public void delete(String... ticketIds) {
        for (String ticketId : ticketIds) {
            Ticket ticket = queryService.findByIdOrNo(ticketId);
            if (!ticket.isDeleted()) continue;

            Query worklogsDelete = entityManager.createQuery("delete from AgentWorklog wl where wl.workspace.ticket.id = :id");
            Query workspaceDelete = entityManager.createQuery("delete from AgentWorkspace ws where ws.ticket.id = :id");
            Query logTicketDelete = entityManager.createQuery("delete from LogTicket lt where lt.ticket.id = :id");
            Query ticketDelete = entityManager.createQuery("delete from Ticket tc where tc.id = :id");

            worklogsDelete.setParameter("id", ticketId);
            workspaceDelete.setParameter("id", ticketId);
            logTicketDelete.setParameter("id", ticketId);
            ticketDelete.setParameter("id", ticketId);

            worklogsDelete.executeUpdate();
            workspaceDelete.executeUpdate();
            logTicketDelete.executeUpdate();
            ticketDelete.executeUpdate();
        }
    }

    @Override
    @Transactional
    public void markDeleted(String... ticketIds) {
        for (String ticketId : ticketIds) {
            Ticket ticket = queryService.findByIdOrNo(ticketId);
            if (ticket.getStatus() != TcStatus.CLOSED) continue;

            repo.deleteById(ticketId);
        }
    }

    @Override
    @Transactional
    public void markDeleted(Instant belowDate) {
        repo.deleteAllByCreatedAtLessThanEqual(belowDate);
    }

    @Override
    @Transactional
    public void markDeleted(InstantFilter date) {
        Specification<Ticket> spec = specBuilder.createSpec(new TicketCriteria()
                .setStatus(new TcStatusFilter().setEq(TcStatus.CLOSED))
                .setCreatedAt(date));
        long deleteCount = repo.delete(spec);
        log.info("MARK DELETED TOTAL - {}", deleteCount);
    }

    @Override
    @Transactional
    public void markDeleted(TicketCriteria criteria) {
        Specification<Ticket> spec = specBuilder.createSpec(criteria);
        long deleteCount = repo.delete(spec);
        log.info("MARK DELETED TOTAL - {}", deleteCount);
    }

    @Override
    @Transactional
    public long markDeleted(Instant from, Instant to) {
        long deleteCount = repo.deleteAllByDeletedIsFalseAndStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(TcStatus.CLOSED, from, to);
        log.info("MARK DELETED TOTAL - {}", deleteCount);
        return deleteCount;
    }

    @Override
    @Transactional
    public void restore(String... ticketIds) {
        repo.restoreByIds(ticketIds);
    }

    @Override
    @Transactional(readOnly = true)
    public File report(TicketCriteria criteria) throws IOException {
        List<Ticket> tickets = queryService.findAll(criteria);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");

        List<List<String>> rows = tickets.stream()
                .map(ticket -> {
                    String issueName = ticket.getIssue().getName();
                    Optional<Issue> issueOpt = issueQueryService.findById(ticket.getIssue().getId());
                    if (issueOpt.isPresent()) {
                        issueName = StringUtils.isBlank(issueOpt.get().getAlias()) ?
                                issueOpt.get().getName() :
                                issueOpt.get().getAlias();
                    }


                    List<String> row = new ArrayList<>();
                    row.add(ticket.getNo());
                    row.add(ticket.getWitel().name());
                    row.add(nonNull(ticket.getSto(), "-"));
                    row.add(nonNull(ticket.getIncidentNo(), "-"));
                    row.add(nonNull(ticket.getServiceNo(), "-"));
                    row.add(ticket.getSource().name());
                    row.add(ticket.getSenderName());
                    row.add(ticket.isGaul() ? "Y" : "N");
                    row.add(issueName);
                    row.add(ticket.getIssue().getProduct().name());

                    row.add(ticket.getCreatedAt()
                            .atZone(ZONE_LOCAL)
                            .format(formatter));
                    if (ticket.getStatus() == TcStatus.CLOSED) {
                        row.add(Optional.ofNullable(ticket.getUpdatedAt())
                                .map(ins -> ins.atZone(ZONE_LOCAL)
                                        .toLocalDateTime()
                                        .format(formatter))
                                .orElse("-"));
                    }
                    else {
                        row.add("-");
                    }
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

    @Override
    public void resendPending() {
        List<TicketSummary> tickets = summaryQueryService.findAll(new TicketSummaryCriteria()
                .setStatus(new TcStatusFilter().setIn(List.of(TcStatus.PENDING)))
                .setWipBy(new StringFilter().setEq(MarsUserContext.getId())));

        for (TicketSummary ticket : tickets) {
            log.info("- Check Ticket NO {}", ticket.getNo());
            if (!ticketConfirmRepo.existsByValueIgnoreCase(ticket.getNo())) {
                try {
                    applicationContext.getBean(PendingFlowService.class)
                            .askPostPending(ticket.getNo());
                }
                catch (Exception ex) {

                }
            }
        }
    }

    private <T> T nonNull(T a, T defaults) {
        return Objects.requireNonNullElse(a, defaults);
    }

}
