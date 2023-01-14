package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.enums.TcStatus;

import java.util.Collection;

public class TcStatusFilter extends EnumFilter<TcStatus> {
    @Override
    public TcStatusFilter setGt(TcStatus greaterThan) {
        return (TcStatusFilter) super.setGt(greaterThan);
    }

    @Override
    public TcStatusFilter setGte(TcStatus greaterThanEqual) {
        return (TcStatusFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public TcStatusFilter setLt(TcStatus lessThan) {
        return (TcStatusFilter) super.setLt(lessThan);
    }

    @Override
    public TcStatusFilter setLte(TcStatus lessThanEqual) {
        return (TcStatusFilter) super.setLte(lessThanEqual);
    }

    @Override
    public TcStatusFilter setEq(TcStatus value) {
        return (TcStatusFilter) super.setEq(value);
    }

    @Override
    public TcStatusFilter setIn(Collection<TcStatus> in) {
        return (TcStatusFilter) super.setIn(in);
    }

    @Override
    public TcStatusFilter setIn(TcStatus... in) {
        return (TcStatusFilter) super.setIn(in);
    }

    @Override
    public TcStatusFilter setSpecified(Boolean nullable) {
        return (TcStatusFilter) super.setSpecified(nullable);
    }

    @Override
    public TcStatusFilter setNegated(boolean negated) {
        return (TcStatusFilter) super.setNegated(negated);
    }
}
