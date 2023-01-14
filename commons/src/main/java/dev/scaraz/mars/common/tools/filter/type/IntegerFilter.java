package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.filter.AbsRangeFilter;

import java.util.Collection;

public class IntegerFilter extends AbsRangeFilter<Integer> {
    @Override
    public IntegerFilter setGt(Integer greaterThan) {
        return (IntegerFilter) super.setGt(greaterThan);
    }

    @Override
    public IntegerFilter setGte(Integer greaterThanEqual) {
        return (IntegerFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public IntegerFilter setLt(Integer lessThan) {
        return (IntegerFilter) super.setLt(lessThan);
    }

    @Override
    public IntegerFilter setLte(Integer lessThanEqual) {
        return (IntegerFilter) super.setLte(lessThanEqual);
    }

    @Override
    public IntegerFilter setEq(Integer value) {
        return (IntegerFilter) super.setEq(value);
    }

    @Override
    public IntegerFilter setIn(Collection<Integer> in) {
        return (IntegerFilter) super.setIn(in);
    }

    @Override
    public IntegerFilter setSpecified(Boolean specified) {
        return (IntegerFilter) super.setSpecified(specified);
    }
}
