package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.annotation.CriteriaField;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.filter.type.EnumFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketAgentCriteria extends AuditableCriteria {

    private EnumFilter<AgStatus> status;

    private UserCriteria user;

    @CriteriaField("user.id")
    private StringFilter userId;

    private TicketCriteria ticket;

    @CriteriaField("ticket.id")
    private StringFilter ticketId;
}
