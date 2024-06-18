package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.core.domain.symptom.Issue;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.spec.IssueSpecBuilder;
import dev.scaraz.mars.core.repository.db.order.IssueRepo;
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
public class IssueQueryServiceImpl implements IssueQueryService {

    private final IssueRepo repo;
    private final IssueSpecBuilder specBuilder;

    @Override
    public Optional<Issue> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Optional<Issue> findOne(IssueCriteria criteria) {
        return repo.findOne(specBuilder.createSpec(criteria));
    }

    @Override
    public List<Issue> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Issue> findAllNotDeleted() {
        return repo.findAllByDeletedIsFalse();
    }

    @Override
    public Page<Issue> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Issue> findAll(IssueCriteria criteria) {
        interceptCriteria(criteria);
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Issue> findAll(IssueCriteria criteria, Pageable pageable) {
        interceptCriteria(criteria);
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(IssueCriteria criteria) {
        interceptCriteria(criteria);
        return repo.count(specBuilder.createSpec(criteria));
    }

    private void interceptCriteria(IssueCriteria criteria) {
        if (criteria.getDeleted() != null) return;
        criteria.setDeleted(new BooleanFilter().setEq(false));
    }

}
