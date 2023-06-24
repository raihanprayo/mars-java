package dev.scaraz.mars.core.v2.util.lambda;

@FunctionalInterface
public interface DynamicValueDeserializer<T> {

    T get(String value);

    DynamicValueDeserializer<Boolean> BOOL = v -> v.equalsIgnoreCase("t");

}
