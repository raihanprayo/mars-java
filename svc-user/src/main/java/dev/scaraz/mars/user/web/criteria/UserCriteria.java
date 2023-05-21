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
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria extends AuditableCriteria {
    private StringFilter id;
    private StringFilter nik;
    private StringFilter name;
    private StringFilter email;
    private StringFilter phone;
    private BooleanFilter enabled;

    private WitelFilter witel;
    private LongFilter tgId;
    private StringFilter tgUsername;

    private RoleCriteria role;
}
