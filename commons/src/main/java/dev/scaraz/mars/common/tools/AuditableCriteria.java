package dev.scaraz.mars.common.tools;

import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AuditableCriteria extends TimestampCriteria {

    private StringFilter createdBy;
    private StringFilter updatedBy;
}
