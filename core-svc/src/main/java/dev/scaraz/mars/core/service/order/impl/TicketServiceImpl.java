package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.repository.order.TicketAgentRepo;
import dev.scaraz.mars.core.repository.order.TicketRepo;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepo repo;
    private final TicketAgentRepo agentRepo;

    private final TelegramBotService bot;

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
        String todayStr = todayLd.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String result = todayStr + StringUtils.leftPad(total + "", 6, "0");
        log.debug("Generating no ticket {}", result);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Ticket take(Ticket ticket) {

        User user = SecurityUtil.getCurrentUser();
        agentRepo.save(TicketAgent.builder()
                .ticket(ticket)
                .user(user)
                .status(AgStatus.PROGRESS)
                .build());

        try {
            String message = Translator.tr("ticket.new.agent",
                    ticket.getNo(), user.getName());

            bot.getClient().execute(SendMessage.builder()
                    .chatId(ticket.getSenderId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(message))
                    .build());
        }
        catch (TelegramApiException e) {
            throw BadRequestException.args("Unable to notify user requestor");
        }

        ticket.setStatus(TcStatus.PROGRESS);
        return save(ticket);
    }

}
