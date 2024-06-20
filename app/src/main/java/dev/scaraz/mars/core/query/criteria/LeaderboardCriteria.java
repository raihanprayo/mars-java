package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.*;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LeaderboardCriteria implements Criteria {

    private LongFilter id;
    private StringFilter ticketId;

    private LongFilter solutionId;

    private StringFilter issueId;

    private TcStatusFilter takeStatus;
    private TcStatusFilter closeStatus;

    private StringFilter agId;
    private WitelFilter agWitel;

    private StringFilter rqId;

    private BooleanFilter lastTicketWork;
    private BooleanFilter lastAgentWork;

    private InstantFilter tcCreatedAt;

    public LeaderboardCriteria dup() {
        return new LeaderboardCriteria()
                .setId(id)
                .setTicketId(ticketId)
                .setSolutionId(solutionId)
                .setIssueId(issueId)
                .setTakeStatus(takeStatus)
                .setCloseStatus(closeStatus)
                .setAgId(agId)
                .setAgWitel(agWitel)
                .setRqId(rqId)
                .setLastTicketWork(lastTicketWork)
                .setLastAgentWork(lastAgentWork)
                .setTcCreatedAt(tcCreatedAt);
    }

    public LeaderboardCriteria setId(LongFilter id) {
        this.id = id;
        return this;
    }

    public LeaderboardCriteria setTicketId(StringFilter ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    public LeaderboardCriteria setSolutionId(LongFilter solutionId) {
        this.solutionId = solutionId;
        return this;
    }

    public LeaderboardCriteria setIssueId(StringFilter issueId) {
        this.issueId = issueId;
        return this;
    }

    public LeaderboardCriteria setTakeStatus(TcStatusFilter takeStatus) {
        this.takeStatus = takeStatus;
        return this;
    }

    public LeaderboardCriteria setCloseStatus(TcStatusFilter closeStatus) {
        this.closeStatus = closeStatus;
        return this;
    }

    public LeaderboardCriteria setAgId(StringFilter agId) {
        this.agId = agId;
        return this;
    }

    public LeaderboardCriteria setAgWitel(WitelFilter agWitel) {
        this.agWitel = agWitel;
        return this;
    }

    public LeaderboardCriteria setRqId(StringFilter rqId) {
        this.rqId = rqId;
        return this;
    }

    public LeaderboardCriteria setLastTicketWork(BooleanFilter lastTicketWork) {
        this.lastTicketWork = lastTicketWork;
        return this;
    }

    public LeaderboardCriteria setLastAgentWork(BooleanFilter lastAgentWork) {
        this.lastAgentWork = lastAgentWork;
        return this;
    }

    public LeaderboardCriteria setTcCreatedAt(InstantFilter tcCreatedAt) {
        this.tcCreatedAt = tcCreatedAt;
        return this;
    }
}
