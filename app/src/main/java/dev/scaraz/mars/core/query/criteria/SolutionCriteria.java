package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.TimestampCriteria;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SolutionCriteria extends TimestampCriteria {

    private LongFilter id;

    private StringFilter name;

    private ProductFilter product;

    @Builder.Default
    private BooleanFilter showable = new BooleanFilter().setEq(true);

    private BooleanFilter deleteable;

    public SolutionCriteria setId(LongFilter id) {
        this.id = id;
        return this;
    }

    public SolutionCriteria setName(StringFilter name) {
        this.name = name;
        return this;
    }

    public SolutionCriteria setProduct(ProductFilter product) {
        this.product = product;
        return this;
    }

    public SolutionCriteria setShowable(BooleanFilter showable) {
        this.showable = showable;
        return this;
    }

    public SolutionCriteria setDeleteable(BooleanFilter deleteable) {
        this.deleteable = deleteable;
        return this;
    }
}
