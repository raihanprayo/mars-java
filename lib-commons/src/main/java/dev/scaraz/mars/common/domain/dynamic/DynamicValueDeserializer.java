package dev.scaraz.mars.common.domain.dynamic;

import java.util.List;
import java.util.stream.Collectors;

@FunctionalInterface
public interface DynamicValueDeserializer<T> {

    T get(String value);

    DynamicValueDeserializer<Boolean> BOOL = v -> v.equalsIgnoreCase("t");
    DynamicValueDeserializer<List<String>> LIST_STRING = v -> List.of(v.split("\\|"));

}
