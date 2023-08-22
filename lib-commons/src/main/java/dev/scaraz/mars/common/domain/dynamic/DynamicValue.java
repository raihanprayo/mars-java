package dev.scaraz.mars.common.domain.dynamic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface DynamicValue {

    DynamicType getType();

    String getValue();

    void setValue(Object value);

    @JsonIgnore
    default <X> X getAs(DynamicValueDeserializer<X> converter) {
        if (StringUtils.isBlank(getValue())) return null;

        X x = converter.get(getValue());
        if (getType().isAssignable(x.getClass())) return x;
        throw invalidType();
    }

    @JsonIgnore
    default boolean getAsBoolean() {
        return getAs(DynamicValueDeserializer.BOOL);
    }

    @JsonIgnore
    default int getAsInt() {
        return getAs(Integer::parseInt);
    }

    @JsonIgnore
    default long getAsLong() {
        return getAs(Long::parseLong);
    }

    @JsonIgnore
    default double getAsDouble() {
        return getAs(Double::parseDouble);
    }

    @JsonIgnore
    default float getAsFloat() {
        return getAs(Float::parseFloat);
    }

    @JsonIgnore
    default short getAsShort() {
        return getAs(Short::parseShort);
    }

    @JsonIgnore
    default Duration getAsDuration() {
        return getAs(Duration::parse);
    }

    @JsonIgnore
    default List<String> getAsList() {
        return Optional.ofNullable(getAs(DynamicValueDeserializer.LIST_STRING))
                .orElse(new ArrayList<>());
    }

    default <T> List<T> getAsList(Function<String, T> convert) {
        return getAsList().stream()
                .map(convert)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    default List<Long> getAsLongList() {
        return getAsList(Long::parseLong);
    }

    @JsonIgnore
    default <T extends Enum<T>> T getAsEnum(Class<T> t) {
        if (!getType().isAssignable(Enum.class)) throw invalidType();
        return getAs(v -> Enum.valueOf(t, v));
    }

    static IllegalStateException invalidType() {
        return new IllegalStateException("Invalid conversion");
    }

}
