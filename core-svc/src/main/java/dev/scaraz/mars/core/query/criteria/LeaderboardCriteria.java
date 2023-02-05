package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardCriteria {

    private ProductFilter product;

    private InstantFilter range;

}
