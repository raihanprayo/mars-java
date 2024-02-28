package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.view.WorklogSummary;
import dev.scaraz.mars.core.domain.view.WorklogSummary_;
import dev.scaraz.mars.core.query.criteria.WorklogSummaryCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class WorklogSummarySpecBuilder extends QueryBuilder<WorklogSummary, WorklogSummaryCriteria> {
    @Override
    public Specification<WorklogSummary> createSpec(WorklogSummaryCriteria criteria) {
        return chain()
                .pick(WorklogSummary_.id, criteria.getId())
                .pick(WorklogSummary_.workspaceId, criteria.getWorkspaceId())
                .pick(WorklogSummary_.ticketId, criteria.getTicketId())
                .pick(WorklogSummary_.userId, criteria.getUserId())
                .pick(WorklogSummary_.solution, criteria.getUserId())
                .pick(WorklogSummary_.status, criteria.getStatus())
                .pick(WorklogSummary_.takeStatus, criteria.getTakeStatus())
                .pick(WorklogSummary_.closeStatus, criteria.getCloseStatus())
                .pick(WorklogSummary_.wsCreatedAt, criteria.getWsCreatedAt())
                .pick(WorklogSummary_.wsUpdatedAt, criteria.getWsUpdatedAt())
                .pick(WorklogSummary_.wlCreatedAt, criteria.getWlCreatedAt())
                .pick(WorklogSummary_.wlUpdatedAt, criteria.getWlUpdatedAt())
                .specification();
    }
}
