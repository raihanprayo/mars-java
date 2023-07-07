package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.query.AgentWorkspaceQueryService;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import dev.scaraz.mars.core.query.spec.AgentWorkspaceSpecBuilder;
import dev.scaraz.mars.core.repository.order.AgentWorkspaceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentWorkspaceQueryServiceImpl implements AgentWorkspaceQueryService {

    private final AgentWorkspaceRepo repo;
    private final AgentWorkspaceSpecBuilder specBuilder;


    @Override
    public List<AgentWorkspace> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<AgentWorkspace> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<AgentWorkspace> findAll(AgentWorkspaceCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<AgentWorkspace> findAll(AgentWorkspaceCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(AgentWorkspaceCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }
}
