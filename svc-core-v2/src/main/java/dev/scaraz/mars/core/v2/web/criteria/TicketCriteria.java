package dev.scaraz.mars.core.v2.web.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketCriteria extends AuditableCriteria {

    private StringFilter id;
    private StringFilter no;
    private TcStatusFilter status;
    private TcSourceFilter source;
    private IntegerFilter gaul;
    private StringFilter incidentNo;
    private StringFilter serviceNo;
    private ProductFilter product;
    private StringFilter issue;
    private WitelFilter witel;
    private StringFilter sto;
    private StringFilter senderName;
    private LongFilter senderTg;

}
