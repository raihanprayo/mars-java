package dev.scaraz.mars.common.tools;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class TimestampCriteria implements Criteria {

    private InstantFilter createdAt;
    private InstantFilter updatedAt;

}