package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.WitelFilter;
import lombok.*;

@Getter
@Setter
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

    private RoleCriteria roles;

    public UserCriteria setId(StringFilter id) {
        this.id = id;
        return this;
    }

    public UserCriteria setName(StringFilter name) {
        this.name = name;
        return this;
    }

    public UserCriteria setNik(StringFilter nik) {
        this.nik = nik;
        return this;
    }

    public UserCriteria setPhone(StringFilter phone) {
        this.phone = phone;
        return this;
    }

    public UserCriteria setEmail(StringFilter email) {
        this.email = email;
        return this;
    }

    public UserCriteria setWitel(WitelFilter witel) {
        this.witel = witel;
        return this;
    }

    public UserCriteria setSto(StringFilter sto) {
        this.sto = sto;
        return this;
    }

    public UserCriteria setActive(BooleanFilter active) {
        this.active = active;
        return this;
    }

    public UserCriteria setTg(UserTgCriteria tg) {
        this.tg = tg;
        return this;
    }

    public UserCriteria setRoles(RoleCriteria roles) {
        this.roles = roles;
        return this;
    }
}
