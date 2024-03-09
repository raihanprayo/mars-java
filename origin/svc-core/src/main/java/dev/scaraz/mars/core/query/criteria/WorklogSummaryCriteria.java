package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.AgStatusFilter;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import lombok.Getter;

@Getter
public class WorklogSummaryCriteria implements Criteria {

    private StringFilter id;
    private StringFilter workspaceId;
    private StringFilter ticketId;
    private StringFilter userId;
    private AgStatusFilter status;
    private TcStatusFilter takeStatus;
    private TcStatusFilter closeStatus;
    private InstantFilter wsCreatedAt;
    private InstantFilter wsUpdatedAt;
    private StringFilter createdBy;
    private InstantFilter createdAt;
    private StringFilter updatedBy;
    private InstantFilter updatedAt;

    private SolutionCriteria solution;

    private TicketCriteria ticket;

    public WorklogSummaryCriteria setId(StringFilter id) {
        this.id = id;
        return this;
    }

    public WorklogSummaryCriteria setWorkspaceId(StringFilter workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    public WorklogSummaryCriteria setTicketId(StringFilter ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    public WorklogSummaryCriteria setUserId(StringFilter userId) {
        this.userId = userId;
        return this;
    }

    public WorklogSummaryCriteria setStatus(AgStatusFilter status) {
        this.status = status;
        return this;
    }

    public WorklogSummaryCriteria setTakeStatus(TcStatusFilter takeStatus) {
        this.takeStatus = takeStatus;
        return this;
    }

    public WorklogSummaryCriteria setCloseStatus(TcStatusFilter closeStatus) {
        this.closeStatus = closeStatus;
        return this;
    }

    public WorklogSummaryCriteria setWsCreatedAt(InstantFilter wsCreatedAt) {
        this.wsCreatedAt = wsCreatedAt;
        return this;
    }

    public WorklogSummaryCriteria setWsUpdatedAt(InstantFilter wsUpdatedAt) {
        this.wsUpdatedAt = wsUpdatedAt;
        return this;
    }

    public WorklogSummaryCriteria setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public WorklogSummaryCriteria setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public WorklogSummaryCriteria setUpdatedBy(StringFilter updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    public WorklogSummaryCriteria setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public WorklogSummaryCriteria setSolution(SolutionCriteria solution) {
        this.solution = solution;
        return this;
    }
}
