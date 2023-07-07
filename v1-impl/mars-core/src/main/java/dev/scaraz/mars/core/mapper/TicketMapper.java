package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.TicketShortDTO;
import dev.scaraz.mars.core.domain.view.TicketSummary;

public interface TicketMapper {
    TicketShortDTO toShortDTO(TicketSummary summary);
}
