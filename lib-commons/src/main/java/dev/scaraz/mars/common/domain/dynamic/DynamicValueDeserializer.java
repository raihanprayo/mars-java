package dev.scaraz.mars.common.domain.dynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FunctionalInterface
public interface DynamicValueDeserializer<T> {

    T get(String value);

    DynamicValueDeserializer<Boolean> BOOL = v -> v.equalsIgnoreCase("t");
    DynamicValueDeserializer<List<String>> LIST_STRING = v -> {
        String[] splits = v.split("\\|");
        if (splits.length> 0) return new ArrayList<>(Arrays.asList(splits));
        return new ArrayList<>();
    };

}
