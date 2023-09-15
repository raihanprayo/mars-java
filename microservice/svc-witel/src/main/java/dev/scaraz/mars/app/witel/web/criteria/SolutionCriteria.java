package dev.scaraz.mars.app.witel.web.criteria;

import dev.scaraz.mars.common.tools.TimestampCriteria;
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
public class SolutionCriteria extends TimestampCriteria {
    private StringFilter id;
    private StringFilter name;
    private ProductFilter product;
}
