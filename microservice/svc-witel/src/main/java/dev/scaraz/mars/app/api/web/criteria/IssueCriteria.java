package dev.scaraz.mars.app.api.web.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.BigDecimalFilter;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IssueCriteria extends AuditableCriteria {

    private StringFilter id;
    private ProductFilter product;
    private StringFilter code;
    private StringFilter name;
    private StringFilter description;
    private BigDecimalFilter score;
    private BooleanFilter deleted;

}
