package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.EnumFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
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
}
