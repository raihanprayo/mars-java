package dev.scaraz.mars.v1.core.query.criteria;

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
    private StringFilter wipBy;

    private IssueCriteria issue;

    public TicketSummaryCriteria copy() {
        return TicketSummaryCriteria.builder()
                .id(id)
                .no(no)
                .witel(witel)
                .sto(sto)
                .incidentNo(incidentNo)
                .serviceNo(serviceNo)
                .status(status)
                .source(source)
                .gaul(gaul)
                .gaulCount(gaulCount)
                .senderName(senderName)
                .senderId(senderId)
                .agentCount(agentCount)
                .product(product)
                .wip(wip)
                .wipBy(wipBy)
                .build();
    }
}
