package dev.scaraz.mars.app.witel.service.flow;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.app.witel.domain.order.Ticket;
import dev.scaraz.mars.app.witel.service.query.IssueQueryService;
import dev.scaraz.mars.common.domain.request.CreateTicketDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderFlowService {

    private final IssueQueryService issueQueryService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Ticket create(CreateTicketDTO request) {
        Issue issue = issueQueryService.findById(request.getIssue());


    }

}
