package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.query.GroupQueryService;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
import dev.scaraz.mars.core.query.spec.GroupSpecBuilder;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class GroupQueryServiceImpl implements GroupQueryService {

    private final GroupRepo repo;
    private final GroupSpecBuilder specBuilder;

    @Override
    public List<Group> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Group> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Group> findAll(GroupCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Group> findAll(GroupCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(GroupCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }
}
