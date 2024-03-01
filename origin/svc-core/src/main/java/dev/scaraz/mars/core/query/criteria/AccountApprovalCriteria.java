package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.TimestampCriteria;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.WitelFilter;
import lombok.Getter;

@Getter
public class AccountApprovalCriteria extends TimestampCriteria {
    private StringFilter id;
    private StringFilter no;
    private StringFilter status;
    private StringFilter name;
    private StringFilter nik;
    private StringFilter phone;
    private WitelFilter witel;
    private StringFilter sto;

    public AccountApprovalCriteria setId(StringFilter id) {
        this.id = id;
        return this;
    }

    public AccountApprovalCriteria setNo(StringFilter no) {
        this.no = no;
        return this;
    }

    public AccountApprovalCriteria setStatus(StringFilter status) {
        this.status = status;
        return this;
    }

    public AccountApprovalCriteria setName(StringFilter name) {
        this.name = name;
        return this;
    }

    public AccountApprovalCriteria setNik(StringFilter nik) {
        this.nik = nik;
        return this;
    }

    public AccountApprovalCriteria setPhone(StringFilter phone) {
        this.phone = phone;
        return this;
    }

    public AccountApprovalCriteria setWitel(WitelFilter witel) {
        this.witel = witel;
        return this;
    }

    public AccountApprovalCriteria setSto(StringFilter sto) {
        this.sto = sto;
        return this;
    }
}
