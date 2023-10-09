package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;
import dev.scaraz.mars.core.query.criteria.AgentWorklogCriteria;
import dev.scaraz.mars.core.query.spec.AgentSpecBuilder;
import dev.scaraz.mars.core.query.spec.AgentWorklogSpecBuilder;
import dev.scaraz.mars.core.repository.db.order.AgentRepo;
import dev.scaraz.mars.core.repository.db.order.AgentWorklogRepo;
import dev.scaraz.mars.core.repository.db.order.AgentWorkspaceRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class AgentQueryServiceImpl implements AgentQueryService {

    private final AgentRepo repo;
    private final AgentWorkspaceRepo workspaceRepo;
    private final AgentWorklogRepo worklogRepo;

    private final AgentSpecBuilder specBuilder;
    private final AgentWorklogSpecBuilder worklogSpecBuilder;

    @Override
    public List<Agent> findAll() {
        return repo.findAll();
    }
    @Override
    public List<AgentWorklog> findAllWorklogs() {
        return worklogRepo.findAll();
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
    public List<AgentWorklog> findAllWorklogs(AgentWorklogCriteria criteria) {
        return worklogRepo.findAll(worklogSpecBuilder.createSpec(criteria));
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
    public List<AgentWorkspace> findByUserId(String userId) {
        return workspaceRepo.findByAgentUserId(userId);
    }

    @Override
    public List<AgentWorkspace> findWorkspacesByTicket(String ticketIdOrNo) {
        return workspaceRepo.findByTicketIdOrTicketNoOrderByCreatedAt(ticketIdOrNo, ticketIdOrNo);
    }

    @Override
    public List<AgentWorklog> findWorklogsByTicketIdOrNo(String ticketIdOrNo) {
        return worklogRepo.findByWorkspaceTicketIdOrWorkspaceTicketNoOrderByCreatedAt(ticketIdOrNo, ticketIdOrNo);
    }

    @Override
    public AgentWorkspace getLastWorkspace(String ticketId) {
        return workspaceRepo.findFirstByTicketIdOrderByCreatedAtDesc(ticketId)
                .map(workspace -> {
                    if (workspace.getStatus() == AgStatus.CLOSED)
                        throw new BadRequestException("Agen Workspace telah ditutup");
                    return workspace;
                })
                .orElseThrow(() -> NotFoundException.entity(AgentWorkspace.class, "ticket", ticketId));
    }

    @Override
    public boolean isWorkInProgress(String ticketId) {
        try {
            UUID.fromString(ticketId);
            return workspaceRepo.existsByTicketIdAndStatus(
                    ticketId,
                    AgStatus.PROGRESS);
        }
        catch (IllegalArgumentException ex) {
            return workspaceRepo.existsByTicketNoAndStatus(
                    ticketId,
                    AgStatus.PROGRESS);
        }
    }

}
