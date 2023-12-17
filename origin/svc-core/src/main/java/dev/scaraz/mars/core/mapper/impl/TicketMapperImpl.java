package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.TicketDetailDTO;
import dev.scaraz.mars.common.domain.response.TicketShortDTO;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.mapper.TicketMapper;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.AgentWorklogQueryService;
import dev.scaraz.mars.core.query.AgentWorkspaceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TicketMapperImpl implements TicketMapper {

    private final AgentQueryService agentQueryService;
    private final AgentWorklogQueryService agentWorklogQueryService;
    private final AgentWorkspaceQueryService agentWorkspaceQueryService;

    @Override
    public TicketShortDTO toShortDTO(TicketSummary summary) {
        return TicketShortDTO.builder()
                .id(summary.getId())
                .no(summary.getNo())
                .status(summary.getStatus())
                .createdAt(summary.getCreatedAt())
                .updatedAt(summary.getUpdatedAt())
                .build();
    }


    @Transactional(readOnly = true)
    public TicketDetailDTO toDetailDTO(TicketSummary summary) {
        AgentWorkspace workspace;
        try {
            workspace = agentQueryService.getLastWorkspace(summary.getId());
        } catch (Exception ex) {
            workspace = null;
        }
        return TicketDetailDTO.builder()

                .build();
    }

}
