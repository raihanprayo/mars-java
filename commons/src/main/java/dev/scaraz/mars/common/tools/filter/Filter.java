package dev.scaraz.mars.common.tools.filter;

import java.io.Serializable;
import java.util.Collection;

public interface Filter<T> extends Serializable {
    Filter<T> setEq(T value);

    Filter<T> setIn(Collection<T> value);

    Filter<T> setSpecified(Boolean specified);

    T getEq();
    Collection<T> getIn();

    Filter<T> setNegated(boolean negated);
    boolean isNegated();

    Boolean getSpecified();
}
