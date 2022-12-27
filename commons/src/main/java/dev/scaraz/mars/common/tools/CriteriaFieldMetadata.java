package dev.scaraz.mars.common.tools;

import dev.scaraz.mars.common.tools.filter.Criteria;
import dev.scaraz.mars.common.tools.filter.Filter;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaFieldMetadata {
    private int depth;
    private String attribute;
    private Filter<?> filter;

    public boolean isNestedField() {
        return depth > 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CriteriaFieldMetadata)) return false;

        CriteriaFieldMetadata that = (CriteriaFieldMetadata) o;

        return new EqualsBuilder().append(getDepth(), that.getDepth()).append(getAttribute(), that.getAttribute()).append(getFilter(), that.getFilter()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getDepth()).append(getAttribute()).append(getFilter()).toHashCode();
    }
}
