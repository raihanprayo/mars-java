package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.repository.order.IssueRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class IssueQueryServiceImpl extends QueryBuilder implements IssueQueryService {

    private final IssueRepo issueRepo;

    @Override
    public Optional<Issue> findById(String id) {
        return issueRepo.findById(id);
    }

    @Override
    public Optional<Issue> findOne(IssueCriteria criteria) {
        return issueRepo.findOne(createSpecification(criteria));
    }

    @Override
    public List<Issue> findAll() {
        return issueRepo.findAll();
    }

    @Override
    public Page<Issue> findAll(Pageable pageable) {
        return issueRepo.findAll(pageable);
    }

    @Override
    public List<Issue> findAll(IssueCriteria criteria) {
        return issueRepo.findAll(createSpecification(criteria));
    }

    @Override
    public Page<Issue> findAll(IssueCriteria criteria, Pageable pageable) {
        return issueRepo.findAll(createSpecification(criteria), pageable);
    }

}
