package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace;
import dev.scaraz.mars.core.query.AgentWorkspaceQueryService;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import dev.scaraz.mars.core.query.spec.AgentWorkspaceSpecBuilder;
import dev.scaraz.mars.core.repository.db.order.AgentWorkspaceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


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


    @Override
    public List<AgentWorkspace> findWorkspacesByTicket(String ticketIdOrNo) {
        return repo.findByTicketIdOrTicketNoOrderByCreatedAt(ticketIdOrNo, ticketIdOrNo);
    }

    @Override
    public AgentWorkspace getLastWorkspace(String ticketId) {
        return getLastWorkspace(ticketId, false);
    }

    @Override
    public AgentWorkspace getLastWorkspace(String ticketId, boolean bypass) throws NotFoundException {
        return getLastWorkspaceOptional(ticketId, bypass)
                .orElseThrow(() -> NotFoundException.entity(AgentWorkspace.class, "ticket", ticketId));
    }

    @Override
    public Optional<AgentWorkspace> getLastWorkspaceOptional(String ticketId) {
        return getLastWorkspaceOptional(ticketId, true);
    }

    @Override
    public Optional<AgentWorkspace> getLastWorkspaceOptional(String ticketId, boolean bypass) {
        return repo.findFirstByTicketIdOrderByCreatedAtDesc(ticketId)
                .map(workspace -> {
                    if (!bypass && workspace.getStatus() == AgStatus.CLOSED)
                        throw new BadRequestException("Workspace Agent telah ditutup");
                    return workspace;
                });
    }

    @Override
    public boolean isWorkInProgress(String ticketId) {
        try {
            UUID.fromString(ticketId);
            return repo.existsByTicketIdAndStatus(
                    ticketId,
                    AgStatus.PROGRESS);
        }
        catch (IllegalArgumentException ex) {
            return repo.existsByTicketNoAndStatus(
                    ticketId,
                    AgStatus.PROGRESS);
        }
    }

}
