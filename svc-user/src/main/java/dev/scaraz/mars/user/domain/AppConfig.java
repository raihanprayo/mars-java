package dev.scaraz.mars.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_app_config")
public class AppConfig extends AuditableEntity {

    private static final Gson gson = new Gson();

    public enum Type {
        STRING,
        NUMBER,
        BOOLEAN,
        JSON,
        DURATION,
        ARRAY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(updatable = false, unique = true)
    private String name;

    @Column
    private String title;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "class_type")
    private String classType;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String value;

    @Column
    private String description;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_category_id")
    private AppConfigCategory category;

    public void setValue(String value) {
        this.value = value;
    }

    public void setAsString(String value) {
        type = Type.STRING;
        classType = String.class.getCanonicalName();
        this.value = value;
    }

    public void setAsBoolean(boolean value) {
        type = Type.BOOLEAN;
        classType = Boolean.class.getCanonicalName();
        this.value = value ? "t" : "f";
    }

    public void setAsNumber(Number value) {
        type = Type.NUMBER;
        if (value != null) {
            classType = value.getClass().getCanonicalName();
            this.value = value.toString();
        }
        else {
            classType = Integer.class.getCanonicalName();
            this.value = Integer.toString(0);
        }
    }

    public void setAsJson(Serializable value) {
        type = Type.JSON;
        classType = value.getClass().getCanonicalName();
        this.value = gson.toJson(value);
    }

    public void setAsDuration(Duration duration) {
        type = Type.DURATION;
        classType = Duration.class.getCanonicalName();
        this.value = duration.toString();
    }

    public void setAsArray(Iterable<String> values) {
        type = Type.ARRAY;
        classType = List.class.getCanonicalName();
        this.value = String.join("|", values);
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public Boolean getAsBoolean() {
        if (value.length() == 1) return value.equalsIgnoreCase("t");
        return Boolean.parseBoolean(value);
    }

    @JsonIgnore
    public Number getAsNumber() {
        try {
            switch (type) {
                case NUMBER:
                    Class<?> aClass = getClass().getClassLoader().loadClass(classType);
                    if (aClass != null && ClassUtils.isAssignable(Number.class, aClass)) {
                        if (aClass == Integer.class) return Integer.parseInt(value);
                        if (aClass == Double.class) return Double.parseDouble(value);
                        if (aClass == Float.class) return Float.parseFloat(value);
                        if (aClass == Short.class) return Short.parseShort(value);
                        if (aClass == Long.class) return Long.parseLong(value);
                        if (aClass == BigInteger.class) return new BigInteger(value);
                        if (aClass == BigDecimal.class) return new BigDecimal(value);
                    }
                    break;
                case DURATION:
                    return Duration.parse(value).toMillis();
            }

            throw new IllegalStateException("Unable to convert to number type");
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @JsonIgnore
    public Object getAsJson() {
        try {
            Class<?> aClass = getClass().getClassLoader().loadClass(classType);
            return gson.fromJson(value, aClass);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @JsonIgnore
    public Duration getAsDuration() {
        if (!isDuration()) throw new IllegalStateException("Unable to convert value as Duration type");
        return Duration.parse(this.value);
    }

    @JsonIgnore
    public List<String> getAsArray() {
        if (!isArray()) throw new IllegalStateException("Unable to convert value as Array type");

        if (isNull()) return new ArrayList<>();
        return Stream.of(value.split("\\|"))
                .filter(StringUtils::isNoneBlank)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public void getAsArray(Consumer<List<String>> consumeValues) {
        if (isArray()) {
            List<String> list = new ArrayList<>();
            if (isNull()) consumeValues.accept(list);
            else consumeValues.accept(Stream.of(value.split("\\|"))
                    .filter(StringUtils::isNoneBlank)
                    .collect(Collectors.toList()));
        }
    }

    @JsonIgnore
    public boolean isNull() {
        return value == null;
    }

    @JsonIgnore
    public boolean isString() {
        return type == Type.STRING;
    }

    @JsonIgnore
    public boolean isBoolean() {
        return type == Type.BOOLEAN;
    }

    @JsonIgnore
    public boolean isNumber() {
        return type == Type.NUMBER;
    }

    @JsonIgnore
    public boolean isJson() {
        return type == Type.JSON;
    }


    @JsonIgnore
    public boolean isDuration() {
        return type == Type.DURATION;
    }


    @JsonIgnore
    public boolean isArray() {
        return type == Type.ARRAY;
    }

}
