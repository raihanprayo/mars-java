package dev.scaraz.mars.user.web.criteria;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.IntegerFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.WitelFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StoCriteria implements Criteria {
    public IntegerFilter id;
    public WitelFilter witel;
    public StringFilter datel;
    public StringFilter alias;
    public StringFilter name;
}
