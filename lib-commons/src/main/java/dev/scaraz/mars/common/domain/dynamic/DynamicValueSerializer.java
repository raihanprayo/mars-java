package dev.scaraz.mars.common.domain.dynamic;

@FunctionalInterface
public interface DynamicValueSerializer<T> {

    String get(T value);

    DynamicValueSerializer<Boolean> BOOL = v -> v ? "t" : "f";
}
