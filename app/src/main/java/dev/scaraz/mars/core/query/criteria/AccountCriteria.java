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
public class AccountCriteria extends AuditableCriteria {

    private StringFilter id;
    private StringFilter name;
    private StringFilter nik;
    private StringFilter phone;
    private StringFilter email;
    private WitelFilter witel;
    private StringFilter sto;

    private BooleanFilter active;
    private AccountTgCriteria tg;

    private RoleCriteria roles;

    public AccountCriteria setId(StringFilter id) {
        this.id = id;
        return this;
    }

    public AccountCriteria setName(StringFilter name) {
        this.name = name;
        return this;
    }

    public AccountCriteria setNik(StringFilter nik) {
        this.nik = nik;
        return this;
    }

    public AccountCriteria setPhone(StringFilter phone) {
        this.phone = phone;
        return this;
    }

    public AccountCriteria setEmail(StringFilter email) {
        this.email = email;
        return this;
    }

    public AccountCriteria setWitel(WitelFilter witel) {
        this.witel = witel;
        return this;
    }

    public AccountCriteria setSto(StringFilter sto) {
        this.sto = sto;
        return this;
    }

    public AccountCriteria setActive(BooleanFilter active) {
        this.active = active;
        return this;
    }

    public AccountCriteria setTg(AccountTgCriteria tg) {
        this.tg = tg;
        return this;
    }

    public AccountCriteria setRoles(RoleCriteria roles) {
        this.roles = roles;
        return this;
    }
}
