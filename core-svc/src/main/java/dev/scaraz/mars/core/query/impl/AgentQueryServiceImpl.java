package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;
import dev.scaraz.mars.core.query.spec.TicketAgentSpecBuilder;
import dev.scaraz.mars.core.repository.order.AgentRepo;
import dev.scaraz.mars.core.repository.order.AgentWorkspaceRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class AgentQueryServiceImpl implements AgentQueryService {

    private final AgentRepo repo;
    private final AgentWorkspaceRepo workspaceRepo;

    private final TicketAgentSpecBuilder specBuilder;

    @Override
    public List<Agent> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Agent> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Agent> findAll(AgentCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Agent> findAll(AgentCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(AgentCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }


    @Override
    public AgentWorkspace getLastWorkspace(String ticketId) {
        return workspaceRepo.findFirstByTicketIdOrderByCreatedAtDesc(ticketId)
                .orElseThrow(() -> NotFoundException.entity(AgentWorkspace.class, "ticket", ticketId));
    }

    @Override
    public boolean isWorkInProgress(String ticketId) {
        return workspaceRepo.existsByTicketIdAndStatus(ticketId, AgStatus.PROGRESS);
    }

}
