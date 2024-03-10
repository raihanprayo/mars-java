package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketCriteria extends AuditableCriteria {
    private StringFilter id;
    private WitelFilter witel;
    private StringFilter sto;
    private StringFilter no;
    private StringFilter incidentNo;
    private StringFilter serviceNo;

    private TcStatusFilter status;
    private TcSourceFilter source;

    private StringFilter senderName;
    private LongFilter senderId;
    private IntegerFilter gaul;

    private ProductFilter product;
    private IssueCriteria issue;
    private AgentCriteria agents;

    private InstantFilter closedAt;

    private BooleanFilter deleted;
    private InstantFilter deletedAt;

    public TicketCriteria setId(StringFilter id) {
        this.id = id;
        return this;
    }

    public TicketCriteria setWitel(WitelFilter witel) {
        this.witel = witel;
        return this;
    }

    public TicketCriteria setSto(StringFilter sto) {
        this.sto = sto;
        return this;
    }

    public TicketCriteria setNo(StringFilter no) {
        this.no = no;
        return this;
    }

    public TicketCriteria setIncidentNo(StringFilter incidentNo) {
        this.incidentNo = incidentNo;
        return this;
    }

    public TicketCriteria setServiceNo(StringFilter serviceNo) {
        this.serviceNo = serviceNo;
        return this;
    }

    public TicketCriteria setStatus(TcStatusFilter status) {
        this.status = status;
        return this;
    }

    public TicketCriteria setSource(TcSourceFilter source) {
        this.source = source;
        return this;
    }

    public TicketCriteria setSenderName(StringFilter senderName) {
        this.senderName = senderName;
        return this;
    }

    public TicketCriteria setSenderId(LongFilter senderId) {
        this.senderId = senderId;
        return this;
    }

    public TicketCriteria setGaul(IntegerFilter gaul) {
        this.gaul = gaul;
        return this;
    }

    public TicketCriteria setProduct(ProductFilter product) {
        this.product = product;
        return this;
    }

    public TicketCriteria setIssue(IssueCriteria issue) {
        this.issue = issue;
        return this;
    }

    public TicketCriteria setAgents(AgentCriteria agents) {
        this.agents = agents;
        return this;
    }

    public TicketCriteria setClosedAt(InstantFilter closedAt) {
        this.closedAt = closedAt;
        return this;
    }

    public TicketCriteria setDeleted(BooleanFilter deleted) {
        this.deleted = deleted;
        return this;
    }

    public TicketCriteria setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }
}
