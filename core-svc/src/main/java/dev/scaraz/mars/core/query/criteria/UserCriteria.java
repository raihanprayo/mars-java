package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.WitelFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria extends AuditableCriteria {

    private StringFilter id;
    private StringFilter name;
    private StringFilter nik;
    private StringFilter phone;
    private StringFilter email;
    private WitelFilter witel;
    private StringFilter sto;

    private BooleanFilter active;
    private UserTgCriteria tg;

    private GroupCriteria group;
    private RoleCriteria roles;
}
