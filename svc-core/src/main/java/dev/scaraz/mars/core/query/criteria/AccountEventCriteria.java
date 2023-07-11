package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEventCriteria implements Criteria {
    private StringFilter id;
    private StringFilter type;

}
