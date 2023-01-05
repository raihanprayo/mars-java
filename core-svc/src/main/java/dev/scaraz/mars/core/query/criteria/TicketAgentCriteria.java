package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.AgStatusFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketAgentCriteria extends AuditableCriteria {

    private StringFilter id;

    private AgStatusFilter status;

    private TcStatusFilter closeStatus;

    private UserCriteria user;
    private TicketCriteria ticket;

    private StringFilter userId;
    private StringFilter ticketId;
    private StringFilter ticketNo;
}
