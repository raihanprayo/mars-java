package dev.scaraz.mars.core.query.criteria;

import dev.scaraz.mars.common.tools.AuditableCriteria;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgentCriteria extends AuditableCriteria {

    private StringFilter id;

    private StringFilter nik;

    private LongFilter telegram;

    private StringFilter userId;

    public AgentCriteria setId(StringFilter id) {
        this.id = id;
        return this;
    }

    public AgentCriteria setNik(StringFilter nik) {
        this.nik = nik;
        return this;
    }

    public AgentCriteria setTelegram(LongFilter telegram) {
        this.telegram = telegram;
        return this;
    }

    public AgentCriteria setUserId(StringFilter userId) {
        this.userId = userId;
        return this;
    }
}
