package dev.scaraz.mars.core.v2.web.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
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
public class StoCriteria extends AuditableCriteria {

    private StringFilter id;
    private StringFilter name;
    private WitelFilter witel;
    private StringFilter datel;

}
