package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.AgStatusFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgentWorkspaceCriteria extends AuditableCriteria {

    private LongFilter id;

    private StringFilter userId;

    private AgStatusFilter status;

    private AgentCriteria agent;

    private TicketCriteria ticket;

    public AgentWorkspaceCriteria setId(LongFilter id) {
        this.id = id;
        return this;
    }

    public AgentWorkspaceCriteria setUserId(StringFilter userId) {
        this.userId = userId;
        return this;
    }

    public AgentWorkspaceCriteria setStatus(AgStatusFilter status) {
        this.status = status;
        return this;
    }

    public AgentWorkspaceCriteria setAgent(AgentCriteria agent) {
        this.agent = agent;
        return this;
    }

    public AgentWorkspaceCriteria setTicket(TicketCriteria ticket) {
        this.ticket = ticket;
        return this;
    }
}