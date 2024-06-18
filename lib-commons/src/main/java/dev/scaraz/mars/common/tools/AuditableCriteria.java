package dev.scaraz.mars.common.tools;

import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuditableCriteria extends TimestampCriteria {

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
