package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.filter.AbsRangeFilter;

import java.util.Collection;

public class LongFilter extends AbsRangeFilter<Long> {
    @Override
    public LongFilter setGt(Long greaterThan) {
        return (LongFilter) super.setGt(greaterThan);
    }

    @Override
    public LongFilter setGte(Long greaterThanEqual) {
        return (LongFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public LongFilter setLt(Long lessThan) {
        return (LongFilter) super.setLt(lessThan);
    }

    @Override
    public LongFilter setLte(Long lessThanEqual) {
        return (LongFilter) super.setLte(lessThanEqual);
    }

    @Override
    public LongFilter setEq(Long value) {
        return (LongFilter) super.setEq(value);
    }

    @Override
    public LongFilter setIn(Collection<Long> in) {
        return (LongFilter) super.setIn(in);
    }

    @Override
    public LongFilter setSpecified(Boolean specified) {
        return (LongFilter) super.setSpecified(specified);
    }
}
