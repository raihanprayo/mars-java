package dev.scaraz.mars.common.utils;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class ConfigEntry<T> {
    private final String key;
    private final Supplier<T> value;
    private final String description;

    public ConfigEntry(String key, Supplier<T> value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    public ConfigEntry(String key, Supplier<T> value) {
        this(key, value, null);
    }

    public ConfigEntry(String key, T value, String description) {
        this(key, () -> value, description);
    }

    public ConfigEntry(String key, T value) {
        this(key, () -> value);
    }
}
