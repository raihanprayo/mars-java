package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.*;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketSummaryCriteria extends AuditableCriteria {

    private StringFilter id;
    private StringFilter no;
    private WitelFilter witel;
    private StringFilter sto;
    private StringFilter incidentNo;
    private StringFilter serviceNo;

    private TcStatusFilter status;
    private TcSourceFilter source;
    private BooleanFilter gaul;
    private IntegerFilter gaulCount;

    private StringFilter senderName;
    private LongFilter senderId;

    private IntegerFilter agentCount;
    private ProductFilter product;

    private BooleanFilter wip;
    private UserCriteria wipBy;

}
