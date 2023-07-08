package dev.scaraz.mars.common.domain.dynamic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FunctionalInterface
public interface DynamicValueSerializer<T> {

    String get(T value);

    DynamicValueSerializer<Boolean> BOOL = v -> v ? "t" : "f";
    DynamicValueSerializer<List<?>> LIST_STRING = v -> v.stream()
            .map(Object::toString)
            .collect(Collectors.joining("|"));
}
