package dev.scaraz.mars.common.tools.filter;

import java.util.Collection;

public interface ReadableFilter<T> extends Filter<T> {
    ReadableFilter<T> setLike(T like);

    T getLike();

}
