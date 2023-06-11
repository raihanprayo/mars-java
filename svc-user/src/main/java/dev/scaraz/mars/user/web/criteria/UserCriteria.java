package dev.scaraz.mars.user.web.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
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
public class UserCriteria extends AuditableCriteria {
    private StringFilter id;
    private StringFilter name;
    private StringFilter nik;
    private LongFilter telegram;
    private WitelFilter witel;
    private StringFilter sto;
    private StringFilter phone;
    private StringFilter email;
    private BooleanFilter enabled;

    private RoleCriteria roles;

}

