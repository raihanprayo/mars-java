package dev.scaraz.mars.core.service.query;

import dev.scaraz.mars.core.datasource.domain.Issue;
import dev.scaraz.mars.core.web.criteria.IssueCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IssueQueryService {
    Optional<Issue> findById(Long id);

    Optional<Issue> findOne(IssueCriteria criteria);

    List<Issue> findAll();

    Page<Issue> findAll(Pageable pageable);

    List<Issue> findAll(IssueCriteria criteria);

    Page<Issue> findAll(IssueCriteria criteria, Pageable pageable);

    abstract long count();

    long count(IssueCriteria criteria);
}
