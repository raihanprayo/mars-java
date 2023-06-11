package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.db.Issue;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.repository.db.IssueRepo;
import dev.scaraz.mars.core.web.criteria.IssueCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueQueryServiceImpl implements IssueQueryService {

    private final IssueRepo repo;

    @Override
    public List<Issue> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Issue> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Issue> findAll(IssueCriteria criteria) {
        return repo.findAll();
    }

    @Override
    public Page<Issue> findAll(IssueCriteria criteria, Pageable pageable) {
        return repo.findAll(pageable);
    }
}
