package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria extends AuditableCriteria {
    private StringFilter name;
    private StringFilter nik;
    private StringFilter phone;
    private LongFilter telegramId;
    private BooleanFilter active;

    private GroupCriteria group;
    private UserCredentialCriteria credential;
}
