package dev.scaraz.mars.common.domain.dynamic;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public final class DynamicType implements Serializable {

    private static final Set<DynamicType> REGISTERED = new LinkedHashSet<>();
    public static final DynamicType
            STRING = new DynamicType(String.class),
            CHAR = new DynamicType(Character.class, Character.TYPE),
            BOOLEAN = new DynamicType(Boolean.class, Boolean.TYPE),
            INTEGER = new DynamicType(Integer.class, Integer.TYPE),
            LONG = new DynamicType(Long.class, Long.TYPE),
            DOUBLE = new DynamicType(Double.class, Double.TYPE),
            SHORT = new DynamicType(Short.class, Short.TYPE),
            FLOAT = new DynamicType(Float.class, Float.TYPE);

    @Getter
    private final Class<?> type;

    @Getter
    private final Class<?> primitive;

    private DynamicType(Class<?> type, Class<?> primitive) {
        this.type = type;
        this.primitive = primitive;
        REGISTERED.add(this);
    }

    private DynamicType(Class<?> type) {
        this(type, null);
    }

    public boolean isAssignable(Class<?> type) {
        boolean assignableFrom = this.type.isAssignableFrom(type);
        if (primitive != null)
            return assignableFrom || primitive.isAssignableFrom(type);
        return assignableFrom;
    }

    @Override
    public String toString() {
        return "Dynamic[" + type.getCanonicalName() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DynamicType)) return false;

        DynamicType that = (DynamicType) o;

        return new EqualsBuilder()
                .append(getType(), that.getType())
                .append(getPrimitive(), that.getPrimitive())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getType())
                .append(getPrimitive())
                .toHashCode();
    }

    public static DynamicType of(Class<?> type) {
        for (DynamicType t : REGISTERED) {
            if (t.isAssignable(type)) return t;
        }
        return new DynamicType(type);
    }

    public static DynamicType from(String from) {
        String[] split = from.split("[\\[\\]]");
        String className = split[1];
        try {
            return of(DynamicType.class.getClassLoader().loadClass(className));
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
