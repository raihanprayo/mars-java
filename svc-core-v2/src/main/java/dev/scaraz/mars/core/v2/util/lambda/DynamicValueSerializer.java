package dev.scaraz.mars.core.v2.util.lambda;

import java.lang.reflect.ParameterizedType;

@FunctionalInterface
public interface DynamicValueSerializer<T> {

    String get(T value);

    DynamicValueSerializer<Boolean> BOOL = v -> v ? "t" : "f";
}
