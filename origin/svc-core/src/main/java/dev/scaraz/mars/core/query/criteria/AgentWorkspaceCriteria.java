package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.TimestampCriteria;
import dev.scaraz.mars.common.tools.filter.type.AgStatusFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgentWorkspaceCriteria extends TimestampCriteria {

    private LongFilter id;

    private StringFilter userId;

    private AgStatusFilter status;

    private AgentCriteria agent;

    private TicketCriteria ticket;

}
