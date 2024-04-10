package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.Getter;

@Getter
public class LeaderBoardCriteria extends AuditableCriteria {

    // Leaderboard Criteria
    private StringFilter ticketId;
    private LongFilter workspaceId;
    private StringFilter agentId;

    private IssueCriteria issue;
    private SolutionCriteria solution;

    private InstantFilter lastTicketLogAt;

    @Override
    public LeaderBoardCriteria setCreatedBy(StringFilter createdBy) {
        return (LeaderBoardCriteria) super.setCreatedBy(createdBy);
    }

    @Override
    public LeaderBoardCriteria setUpdatedBy(StringFilter updatedBy) {
        return (LeaderBoardCriteria) super.setUpdatedBy(updatedBy);
    }

    @Override
    public LeaderBoardCriteria setCreatedAt(InstantFilter createdAt) {
        return (LeaderBoardCriteria) super.setCreatedAt(createdAt);
    }

    @Override
    public LeaderBoardCriteria setUpdatedAt(InstantFilter updatedAt) {
        return (LeaderBoardCriteria) super.setUpdatedAt(updatedAt);
    }


    public LeaderBoardCriteria setTicketId(StringFilter ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    public LeaderBoardCriteria setWorkspaceId(LongFilter workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    public LeaderBoardCriteria setAgentId(StringFilter agentId) {
        this.agentId = agentId;
        return this;
    }

    public LeaderBoardCriteria setIssue(IssueCriteria issue) {
        this.issue = issue;
        return this;
    }

    public LeaderBoardCriteria setSolution(SolutionCriteria solution) {
        this.solution = solution;
        return this;
    }

    public LeaderBoardCriteria setLastTicketLogAt(InstantFilter lastTicketLogAt) {
        this.lastTicketLogAt = lastTicketLogAt;
        return this;
    }



}
