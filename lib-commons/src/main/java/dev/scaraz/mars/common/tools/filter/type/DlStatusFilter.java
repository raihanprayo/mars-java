package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.enums.DlStatus;
import dev.scaraz.mars.common.tools.filter.AbsFilter;

import java.util.Collection;

public class DlStatusFilter extends AbsFilter<DlStatus> {

    @Override
    public DlStatusFilter setEq(DlStatus value) {
        return (DlStatusFilter) super.setEq(value);
    }

    @Override
    public DlStatusFilter setIn(Collection<DlStatus> in) {
        return (DlStatusFilter) super.setIn(in);
    }

    @Override
    public DlStatusFilter setSpecified(Boolean specified) {
        return (DlStatusFilter) super.setSpecified(specified);
    }

    @Override
    public DlStatusFilter setNegated(boolean negated) {
        return (DlStatusFilter) super.setNegated(negated);
    }
}
