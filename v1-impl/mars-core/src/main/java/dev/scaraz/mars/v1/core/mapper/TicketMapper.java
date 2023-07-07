package dev.scaraz.mars.v1.core.mapper;

import dev.scaraz.mars.common.domain.response.TicketShortDTO;
import dev.scaraz.mars.v1.core.domain.view.TicketSummary;

public interface TicketMapper {
    TicketShortDTO toShortDTO(TicketSummary summary);
}
