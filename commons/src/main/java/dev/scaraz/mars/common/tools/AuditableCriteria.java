package dev.scaraz.mars.common.tools;

import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditableCriteria extends TimestampCriteria {

    private StringFilter createdBy;
    private StringFilter updatedBy;
}
