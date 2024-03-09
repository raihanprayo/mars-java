package dev.scaraz.mars.common.tools;

import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditableCriteria extends TimestampCriteria {

    private StringFilter createdBy;
    private StringFilter updatedBy;

    public AuditableCriteria setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public AuditableCriteria setUpdatedBy(StringFilter updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }
}
