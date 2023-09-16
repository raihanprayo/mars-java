package dev.scaraz.mars.app.witel.service.query.impl;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.app.witel.repository.IssueParamRepo;
import dev.scaraz.mars.app.witel.repository.IssueRepo;
import dev.scaraz.mars.app.witel.service.query.IssueQueryService;
import dev.scaraz.mars.app.witel.service.query.specification.IssueSpecBuilder;
import dev.scaraz.mars.app.witel.web.criteria.IssueCriteria;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class IssueQueryServiceImpl implements IssueQueryService {

    private final IssueRepo repo;

    private final IssueParamRepo paramRepo;

    private final IssueSpecBuilder specBuilder;

    @Override
    public List<Issue> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Issue> findAll(IssueCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Issue> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<Issue> findAll(IssueCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(IssueCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

    @Override
    public Issue findById(String idOrCode) {
        return repo.findById(idOrCode)
                .orElseThrow(() -> NotFoundException.entity(Issue.class, "id/code", idOrCode));
    }

}
