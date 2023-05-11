package dev.scaraz.mars.core.web.criteria;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.IntegerFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.WitelFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoCriteria implements Criteria {

    private LongFilter id;
    private WitelFilter witel;
    private StringFilter datel;
    private StringFilter alias;
    private StringFilter name;

}
