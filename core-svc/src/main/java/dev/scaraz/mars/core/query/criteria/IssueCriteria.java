package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.EnumFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IssueCriteria extends AuditableCriteria {
    private StringFilter id;
    private StringFilter name;
    private EnumFilter<Product> product;
}
