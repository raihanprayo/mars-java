package dev.scaraz.mars.common.tools;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TimestampCriteria implements Criteria {

    private InstantFilter createdAt;
    private InstantFilter updatedAt;

}
