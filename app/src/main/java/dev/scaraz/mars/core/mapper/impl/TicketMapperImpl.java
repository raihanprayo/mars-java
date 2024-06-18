package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.TicketShortDTO;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.mapper.TicketMapper;
import dev.scaraz.mars.core.query.AgentWorklogQueryService;
import dev.scaraz.mars.core.query.AgentWorkspaceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapperImpl implements TicketMapper {

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


}
