package dev.scaraz.mars.user.web.criteria;

import dev.scaraz.mars.common.tools.TimestampCriteria;
import dev.scaraz.mars.common.tools.filter.type.IntegerFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.WitelFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class StoCriteria extends TimestampCriteria {
    private StringFilter id;
    private StringFilter name;
    private StringFilter datel;
    private WitelFilter witel;
}

