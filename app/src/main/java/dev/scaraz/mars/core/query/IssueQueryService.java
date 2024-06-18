package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.symptom.Issue;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;

import java.util.List;
import java.util.Optional;

public interface IssueQueryService extends BaseQueryService<Issue, IssueCriteria> {
    Optional<Issue> findById(Long id);

    Optional<Issue> findOne(IssueCriteria criteria);

    List<Issue> findAllNotDeleted();
}
