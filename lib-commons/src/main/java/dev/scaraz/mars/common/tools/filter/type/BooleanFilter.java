package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.filter.AbsFilter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import java.util.Collection;

public class BooleanFilter extends AbsFilter<Boolean> {
    @Override
    public BooleanFilter setEq(Boolean value) {
        return (BooleanFilter) super.setEq(value);
    }

    @Override
    public BooleanFilter setIn(Collection<Boolean> in) {
        return (BooleanFilter) super.setIn(in);
    }

    @Override
    public BooleanFilter setSpecified(Boolean specified) {
        return (BooleanFilter) super.setSpecified(specified);
    }

}
