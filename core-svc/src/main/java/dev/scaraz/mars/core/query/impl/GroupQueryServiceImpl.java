package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.query.GroupQueryService;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
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
public class GroupQueryServiceImpl extends QueryBuilder implements GroupQueryService {

    private final GroupRepo repo;

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
        return repo.findAll(createSpecification(criteria));
    }

    @Override
    public Page<Group> findAll(GroupCriteria criteria, Pageable pageable) {
        return repo.findAll(createSpecification(criteria), pageable);
    }

}
