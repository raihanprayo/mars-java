package dev.scaraz.mars.v1.core.query;

import dev.scaraz.mars.v1.core.domain.order.Issue;
import dev.scaraz.mars.v1.core.query.criteria.IssueCriteria;

import java.util.Optional;

public interface IssueQueryService extends BaseQueryService<Issue, IssueCriteria> {
    Optional<Issue> findById(Long id);

    Optional<Issue> findOne(IssueCriteria criteria);

}
