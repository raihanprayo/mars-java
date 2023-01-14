package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.filter.AbsRangeFilter;

import java.time.Instant;
import java.util.Collection;

public class InstantFilter extends AbsRangeFilter<Instant> {
    @Override
    public InstantFilter setGt(Instant greaterThan) {
        return (InstantFilter) super.setGt(greaterThan);
    }

    @Override
    public InstantFilter setGte(Instant greaterThanEqual) {
        return (InstantFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public InstantFilter setLt(Instant lessThan) {
        return (InstantFilter) super.setLt(lessThan);
    }

    @Override
    public InstantFilter setLte(Instant lessThanEqual) {
        return (InstantFilter) super.setLte(lessThanEqual);
    }

    @Override
    public InstantFilter setEq(Instant value) {
        return (InstantFilter) super.setEq(value);
    }

    @Override
    public InstantFilter setIn(Collection<Instant> in) {
        return (InstantFilter) super.setIn(in);
    }

    @Override
    public InstantFilter setSpecified(Boolean specified) {
        return (InstantFilter) super.setSpecified(specified);
    }
}
