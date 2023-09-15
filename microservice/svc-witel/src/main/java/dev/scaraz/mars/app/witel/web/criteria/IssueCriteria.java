package dev.scaraz.mars.app.witel.web.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.*;
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
    private WitelFilter witel;
    private ProductFilter product;
    private StringFilter code;
    private StringFilter name;
    private StringFilter description;
    private BigDecimalFilter score;
    private BooleanFilter deleted;

}
