package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.annotation.CriteriaField;
import dev.scaraz.mars.common.tools.enums.*;
import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.EnumFilter;
import dev.scaraz.mars.common.tools.filter.type.IntegerFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCriteria implements Criteria {
    private StringFilter id;
    private EnumFilter<Witel> witel;
    private StringFilter sto;
    private StringFilter incidentNo;
    private StringFilter serviceNo;
    private EnumFilter<TcStatus> status;
    private EnumFilter<TcSource> source;
    private StringFilter senderName;
    private LongFilter senderId;
    private IntegerFilter gaul;

    private IssueCriteria issue;
    @CriteriaField("issue.product")
    private EnumFilter<Product> product;

    private TicketAgentCriteria agents;

}
