package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.filter.AbsRangeFilter;

import java.math.BigDecimal;
import java.util.Collection;

public class BigDecimalFilter extends AbsRangeFilter<BigDecimal> {

    @Override
    public BigDecimalFilter setGt(BigDecimal greaterThan) {
        return (BigDecimalFilter) super.setGt(greaterThan);
    }

    @Override
    public BigDecimalFilter setGte(BigDecimal greaterThanEqual) {
        return (BigDecimalFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public BigDecimalFilter setLt(BigDecimal lessThan) {
        return (BigDecimalFilter) super.setLt(lessThan);
    }

    @Override
    public BigDecimalFilter setLte(BigDecimal lessThanEqual) {
        return (BigDecimalFilter) super.setLte(lessThanEqual);
    }

    @Override
    public BigDecimalFilter setEq(BigDecimal value) {
        return (BigDecimalFilter) super.setEq(value);
    }

    @Override
    public BigDecimalFilter setIn(Collection<BigDecimal> in) {
        return (BigDecimalFilter) super.setIn(in);
    }

    @Override
    public BigDecimalFilter setSpecified(Boolean specified) {
        return (BigDecimalFilter) super.setSpecified(specified);
    }
}
