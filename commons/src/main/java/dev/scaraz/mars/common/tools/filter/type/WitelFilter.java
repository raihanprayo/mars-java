package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.enums.Witel;

import java.util.Collection;

public class WitelFilter extends EnumFilter<Witel> {
    @Override
    public WitelFilter setGt(Witel greaterThan) {
        return (WitelFilter) super.setGt(greaterThan);
    }

    @Override
    public WitelFilter setGte(Witel greaterThanEqual) {
        return (WitelFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public WitelFilter setLt(Witel lessThan) {
        return (WitelFilter) super.setLt(lessThan);
    }

    @Override
    public WitelFilter setLte(Witel lessThanEqual) {
        return (WitelFilter) super.setLte(lessThanEqual);
    }

    @Override
    public WitelFilter setEq(Witel value) {
        return (WitelFilter) super.setEq(value);
    }

    @Override
    public WitelFilter setIn(Collection<Witel> in) {
        return (WitelFilter) super.setIn(in);
    }

    @Override
    public WitelFilter setIn(Witel... in) {
        return (WitelFilter) super.setIn(in);
    }

    @Override
    public WitelFilter setSpecified(Boolean nullable) {
        return (WitelFilter) super.setSpecified(nullable);
    }

    @Override
    public WitelFilter setNegated(boolean negated) {
        return (WitelFilter) super.setNegated(negated);
    }
}
