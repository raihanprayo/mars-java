package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
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
public class AgentCriteria extends AuditableCriteria {

    private StringFilter id;

    private StringFilter nik;

    private LongFilter telegram;

//    private AgStatusFilter status;
//
//    private TcStatusFilter takeStatus;
//    private TcStatusFilter closeStatus;
//
//    private UserCriteria user;
//    private TicketCriteria ticket;
//
//    private StringFilter userId;
//    private StringFilter ticketId;
//    private StringFilter ticketNo;
}