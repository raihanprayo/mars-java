package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.*;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
public class TicketSummaryCriteria extends AuditableCriteria {

    private StringFilter id;
    private StringFilter no;
    private WitelFilter witel;
    private StringFilter sto;
    private StringFilter incidentNo;
    private StringFilter serviceNo;

    private TcStatusFilter status;
    private TcSourceFilter source;
    private BooleanFilter gaul;
    private IntegerFilter gaulCount;

    private StringFilter senderName;
    private LongFilter senderId;

    private IntegerFilter agentCount;
    private ProductFilter product;

    private BooleanFilter wip;
    private StringFilter wipBy;

    private InstantFilter closedAt;

    private BooleanFilter deleted;
    private InstantFilter deletedAt;

    private IssueCriteria issue;

    private AgentWorkspaceCriteria workspace;

    public TicketSummaryCriteria copy() {
        return new TicketSummaryCriteria()
                .setId(id)
                .setNo(no)
                .setWip(wip)
                .setSto(sto)
                .setStatus(status)
                .setIncidentNo(incidentNo)
                .setServiceNo(serviceNo)
                .setSource(source)
                .setGaul(gaul)
                .setGaulCount(gaulCount)
                .setSenderId(senderId)
                .setSenderName(senderName)
                .setAgentCount(agentCount)
                .setProduct(product)
                .setWip(wip)
                .setWipBy(wipBy)
                .setClosedAt(closedAt)
                .setDeleted(deleted)
                .setDeletedAt(deletedAt)
                .setIssue(issue)
                .setWorkspace(workspace)
                .setCreatedBy(getCreatedBy())
                .setCreatedAt(getCreatedAt())
                .setUpdatedBy(getUpdatedBy())
                .setUpdatedAt(getUpdatedAt());
    }

    public TicketSummaryCriteria setId(StringFilter id) {
        this.id = id;
        return this;
    }

    public TicketSummaryCriteria setNo(StringFilter no) {
        this.no = no;
        return this;
    }

    public TicketSummaryCriteria setWitel(WitelFilter witel) {
        this.witel = witel;
        return this;
    }

    public TicketSummaryCriteria setSto(StringFilter sto) {
        this.sto = sto;
        return this;
    }

    public TicketSummaryCriteria setIncidentNo(StringFilter incidentNo) {
        this.incidentNo = incidentNo;
        return this;
    }

    public TicketSummaryCriteria setServiceNo(StringFilter serviceNo) {
        this.serviceNo = serviceNo;
        return this;
    }

    public TicketSummaryCriteria setStatus(TcStatusFilter status) {
        this.status = status;
        return this;
    }

    public TicketSummaryCriteria setSource(TcSourceFilter source) {
        this.source = source;
        return this;
    }

    public TicketSummaryCriteria setGaul(BooleanFilter gaul) {
        this.gaul = gaul;
        return this;
    }

    public TicketSummaryCriteria setGaulCount(IntegerFilter gaulCount) {
        this.gaulCount = gaulCount;
        return this;
    }

    public TicketSummaryCriteria setSenderName(StringFilter senderName) {
        this.senderName = senderName;
        return this;
    }

    public TicketSummaryCriteria setSenderId(LongFilter senderId) {
        this.senderId = senderId;
        return this;
    }

    public TicketSummaryCriteria setAgentCount(IntegerFilter agentCount) {
        this.agentCount = agentCount;
        return this;
    }

    public TicketSummaryCriteria setProduct(ProductFilter product) {
        this.product = product;
        return this;
    }

    public TicketSummaryCriteria setWip(BooleanFilter wip) {
        this.wip = wip;
        return this;
    }

    public TicketSummaryCriteria setWipBy(StringFilter wipBy) {
        this.wipBy = wipBy;
        return this;
    }

    public TicketSummaryCriteria setClosedAt(InstantFilter closedAt) {
        this.closedAt = closedAt;
        return this;
    }

    public TicketSummaryCriteria setDeleted(BooleanFilter deleted) {
        this.deleted = deleted;
        return this;
    }

    public TicketSummaryCriteria setDeletedAt(InstantFilter deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public TicketSummaryCriteria setIssue(IssueCriteria issue) {
        this.issue = issue;
        return this;
    }

    public TicketSummaryCriteria setWorkspace(AgentWorkspaceCriteria workspace) {
        this.workspace = workspace;
        return this;
    }

    @Override
    public TicketSummaryCriteria setCreatedBy(StringFilter createdBy) {
        return (TicketSummaryCriteria) super.setCreatedBy(createdBy);
    }

    @Override
    public TicketSummaryCriteria setUpdatedBy(StringFilter updatedBy) {
        return (TicketSummaryCriteria) super.setUpdatedBy(updatedBy);
    }

    @Override
    public TicketSummaryCriteria setCreatedAt(InstantFilter createdAt) {
        return (TicketSummaryCriteria) super.setCreatedAt(createdAt);
    }

    @Override
    public TicketSummaryCriteria setUpdatedAt(InstantFilter updatedAt) {
        return (TicketSummaryCriteria) super.setUpdatedAt(updatedAt);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("no", no)
                .append("witel", witel)
                .append("sto", sto)
                .append("incidentNo", incidentNo)
                .append("serviceNo", serviceNo)
                .append("status", status)
                .append("source", source)
                .append("gaul", gaul)
                .append("gaulCount", gaulCount)
                .append("senderName", senderName)
                .append("senderId", senderId)
                .append("agentCount", agentCount)
                .append("product", product)
                .append("wip", wip)
                .append("wipBy", wipBy)
                .append("closedAt", closedAt)
                .append("deleted", deleted)
                .append("deletedAt", deletedAt)
                .append("issue", issue)
                .append("workspace", workspace)
                .append(super.toString())
                .toString();
    }
}
