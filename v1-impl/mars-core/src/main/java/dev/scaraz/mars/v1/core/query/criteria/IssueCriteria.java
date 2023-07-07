package dev.scaraz.mars.v1.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IssueCriteria extends AuditableCriteria {
    private LongFilter id;
    private StringFilter name;
    private ProductFilter product;
    private BooleanFilter deleted;
}
