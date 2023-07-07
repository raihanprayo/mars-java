package dev.scaraz.mars.v1.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.TicketShortDTO;
import dev.scaraz.mars.v1.core.domain.view.TicketSummary;
import dev.scaraz.mars.v1.core.mapper.TicketMapper;
import org.springframework.stereotype.Component;

@Component
public class TicketMapperImpl implements TicketMapper {

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
