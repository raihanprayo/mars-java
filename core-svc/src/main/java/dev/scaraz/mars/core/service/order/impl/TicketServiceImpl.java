package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.core.repository.order.IssueRepo;
import dev.scaraz.mars.core.repository.order.TicketRepo;
import dev.scaraz.mars.core.service.order.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepo repo;
    private final IssueRepo issueRepo;

}
