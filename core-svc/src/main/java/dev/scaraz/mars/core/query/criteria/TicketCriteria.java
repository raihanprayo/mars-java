package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketCriteria extends AuditableCriteria {
    private StringFilter id;
    private WitelFilter witel;
    private StringFilter sto;
    private StringFilter no;
    private StringFilter incidentNo;
    private StringFilter serviceNo;

    private TcStatusFilter status;
    private TcSourceFilter source;

    private StringFilter senderName;
    private LongFilter senderId;
    private IntegerFilter gaul;

    private ProductFilter product;
    private IssueCriteria issue;
    private AgentCriteria agents;
}
