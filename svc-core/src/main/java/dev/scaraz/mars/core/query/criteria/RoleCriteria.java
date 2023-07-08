package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.IntegerFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleCriteria extends AuditableCriteria {
    private StringFilter id;
    private StringFilter name;
}
