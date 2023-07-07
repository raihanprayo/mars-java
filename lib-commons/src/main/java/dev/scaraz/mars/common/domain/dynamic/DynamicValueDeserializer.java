package dev.scaraz.mars.common.domain.dynamic;

@FunctionalInterface
public interface DynamicValueDeserializer<T> {

    T get(String value);

    DynamicValueDeserializer<Boolean> BOOL = v -> v.equalsIgnoreCase("t");

}
