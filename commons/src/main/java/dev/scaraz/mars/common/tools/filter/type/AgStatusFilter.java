package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.Product;

import java.util.Collection;

public class AgStatusFilter extends EnumFilter<AgStatus> {
    @Override
    public AgStatusFilter setGt(AgStatus greaterThan) {
        return (AgStatusFilter) super.setGt(greaterThan);
    }

    @Override
    public AgStatusFilter setGte(AgStatus greaterThanEqual) {
        return (AgStatusFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public AgStatusFilter setLt(AgStatus lessThan) {
        return (AgStatusFilter) super.setLt(lessThan);
    }

    @Override
    public AgStatusFilter setLte(AgStatus lessThanEqual) {
        return (AgStatusFilter) super.setLte(lessThanEqual);
    }

    @Override
    public AgStatusFilter setEq(AgStatus value) {
        return (AgStatusFilter) super.setEq(value);
    }

    @Override
    public AgStatusFilter setIn(Collection<AgStatus> in) {
        return (AgStatusFilter) super.setIn(in);
    }

    @Override
    public AgStatusFilter setIn(AgStatus... in) {
        return (AgStatusFilter) super.setIn(in);
    }

    @Override
    public AgStatusFilter setNullable(boolean nullable) {
        return (AgStatusFilter) super.setNullable(nullable);
    }

    @Override
    public AgStatusFilter setNegated(boolean negated) {
        return (AgStatusFilter) super.setNegated(negated);
    }
}
