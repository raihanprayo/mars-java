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
    private StringFilter solution;
    private AgStatusFilter status;
    private TcStatusFilter takeStatus;
    private TcStatusFilter closeStatus;
    private InstantFilter wsCreatedAt;
    private InstantFilter wsUpdatedAt;
    private InstantFilter wlCreatedAt;
    private InstantFilter wlUpdatedAt;

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

    public WorklogSummaryCriteria setSolution(StringFilter solution) {
        this.solution = solution;
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

    public WorklogSummaryCriteria setWlCreatedAt(InstantFilter wlCreatedAt) {
        this.wlCreatedAt = wlCreatedAt;
        return this;
    }

    public WorklogSummaryCriteria setWlUpdatedAt(InstantFilter wlUpdatedAt) {
        this.wlUpdatedAt = wlUpdatedAt;
        return this;
    }

}
