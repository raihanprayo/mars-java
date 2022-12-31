package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.repository.order.TicketRepo;
import dev.scaraz.mars.core.service.order.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepo repo;
    private final IssueQueryService issueQueryService;

    @Override
    public Ticket save(Ticket ticket) {
        if (ticket.getNo() == null) ticket.setNo(generateTicketNo());
        return repo.save(ticket);
    }

    public String generateTicketNo() {
        LocalDate todayLd = LocalDate.now();

        Instant todayIns = todayLd.atStartOfDay().toInstant(ZoneOffset.of("+07"));

        long total = repo.countByCreatedAtGreaterThanEqual(todayIns);
        String todayStr = todayLd.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        log.debug("Generating no ticket {}", todayStr);
        return todayStr + StringUtils.leftPad(total + "", 6, "0");
    }

}
