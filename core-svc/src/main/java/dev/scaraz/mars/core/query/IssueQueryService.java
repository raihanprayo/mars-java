package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IssueQueryService {
    Optional<Issue> findById(String id);

    Optional<Issue> findOne(IssueCriteria criteria);

    List<Issue> findAll();

    Page<Issue> findAll(Pageable pageable);

    List<Issue> findAll(IssueCriteria criteria);

    Page<Issue> findAll(IssueCriteria criteria, Pageable pageable);
}
