package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.enums.TcSource;

import java.util.Collection;

public class TcSourceFilter extends EnumFilter<TcSource> {
    @Override
    public TcSourceFilter setGt(TcSource greaterThan) {
        return (TcSourceFilter) super.setGt(greaterThan);
    }

    @Override
    public TcSourceFilter setGte(TcSource greaterThanEqual) {
        return (TcSourceFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public TcSourceFilter setLt(TcSource lessThan) {
        return (TcSourceFilter) super.setLt(lessThan);
    }

    @Override
    public TcSourceFilter setLte(TcSource lessThanEqual) {
        return (TcSourceFilter) super.setLte(lessThanEqual);
    }

    @Override
    public TcSourceFilter setEq(TcSource value) {
        return (TcSourceFilter) super.setEq(value);
    }

    @Override
    public TcSourceFilter setIn(Collection<TcSource> in) {
        return (TcSourceFilter) super.setIn(in);
    }

    @Override
    public TcSourceFilter setSpecified(Boolean nullable) {
        return (TcSourceFilter) super.setSpecified(nullable);
    }

    @Override
    public TcSourceFilter setNegated(boolean negated) {
        return (TcSourceFilter) super.setNegated(negated);
    }
}
