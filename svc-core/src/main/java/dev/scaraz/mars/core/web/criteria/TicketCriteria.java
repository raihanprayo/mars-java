package dev.scaraz.mars.core.web.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketCriteria extends AuditableCriteria {
    private StringFilter no;
    private StringFilter incidentNo;
    private StringFilter serviceNo;
    private TcStatusFilter status;
    private BooleanFilter gaul;
    private ProductFilter product;
    private StringFilter issue;
    private InstantFilter closedAt;
    private WitelFilter witel;
    private StringFilter sto;
    private StringFilter senderName;
    private LongFilter senderTgId;
    private TcSourceFilter source;
}
