package dev.scaraz.mars.common.tools;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TimestampCriteria implements Criteria {

    private InstantFilter createdAt;
    private InstantFilter updatedAt;

    public TimestampCriteria setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TimestampCriteria setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}
