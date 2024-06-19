package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.agent.AgentWorklog;
import dev.scaraz.mars.core.domain.agent.AgentWorklog_;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace_;
import dev.scaraz.mars.core.domain.credential.AccountTg_;
import dev.scaraz.mars.core.domain.credential.Account_;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.query.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor

@Component
public class AgentWorklogSpecBuilder extends AuditableSpec<AgentWorklog, AgentWorklogCriteria> {

    @Override
    public Specification<AgentWorklog> createSpec(AgentWorklogCriteria criteria) {
        SpecChain<AgentWorklog> chain = chain();
        if (criteria != null) {
            chain.pick(AgentWorklog_.id, criteria.getId())
                    .pick(AgentWorklog_.takeStatus, criteria.getTakeStatus())
                    .pick(AgentWorklog_.closeStatus, criteria.getCloseStatus())
                    .extend(s -> auditSpec(s, criteria));

            if (criteria.getSolution() != null) {
                SolutionCriteria solution = criteria.getSolution();
                chain.pick(solution.getId(), path(AgentWorklog_.solution, WlSolution_.id));
                chain.pick(solution.getName(), path(AgentWorklog_.solution, WlSolution_.name));
            }

            if (criteria.getWorkspace() != null) {
                AgentWorkspaceCriteria ws = criteria.getWorkspace();
                chain.pick(ws.getId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.id))
                        .pick(ws.getStatus(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.status));
//                        .pick(ws.getUserId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.agent).get(Agent_.userId));


                if (ws.getAccount() != null) {
                    AccountCriteria ag = ws.getAccount();
                    chain.pick(ag.getId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.account).get(Account_.id))
                            .pick(ag.getNik(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.account).get(Account_.nik));

                    if (ag.getTg() != null) {
                        AccountTgCriteria tg = ag.getTg();
                        chain.pick(tg.getId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.account).get(Account_.tg).get(AccountTg_.id));
                    }
                }

                if (ws.getTicket() != null) {
                    TicketCriteria tc = ws.getTicket();
                    chain.pick(tc.getId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.id))
                            .pick(tc.getNo(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.no))
                            .pick(tc.getWitel(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.witel))
                            .pick(tc.getSto(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.sto))
                            .pick(tc.getIncidentNo(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.incidentNo))
                            .pick(tc.getServiceNo(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.serviceNo))
                            .pick(tc.getStatus(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.status))
                            .pick(tc.getSource(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.source))
                            .pick(tc.getGaul(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.gaul))
                            .pick(tc.getSenderId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.senderId))
                            .pick(tc.getSenderName(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.senderName))
                            .pick(tc.getProduct(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.issue).get(TcIssue_.product));
                }
            }
        }
        return chain.specification();
    }
}
