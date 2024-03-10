package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgentWorklogCriteria extends AuditableCriteria {

    private LongFilter id;

    private TcStatusFilter takeStatus;

    private TcStatusFilter closeStatus;

    private AgentWorkspaceCriteria workspace;

    private SolutionCriteria solution;

    public AgentWorklogCriteria setId(LongFilter id) {
        this.id = id;
        return this;
    }

    public AgentWorklogCriteria setTakeStatus(TcStatusFilter takeStatus) {
        this.takeStatus = takeStatus;
        return this;
    }

    public AgentWorklogCriteria setCloseStatus(TcStatusFilter closeStatus) {
        this.closeStatus = closeStatus;
        return this;
    }

    public AgentWorklogCriteria setWorkspace(AgentWorkspaceCriteria workspace) {
        this.workspace = workspace;
        return this;
    }

    public AgentWorklogCriteria setSolution(SolutionCriteria solution) {
        this.solution = solution;
        return this;
    }
}
