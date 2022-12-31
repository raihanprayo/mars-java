package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IssueQueryService extends BaseQueryService<Issue, IssueCriteria> {
    Optional<Issue> findById(String id);

    Optional<Issue> findOne(IssueCriteria criteria);

}
