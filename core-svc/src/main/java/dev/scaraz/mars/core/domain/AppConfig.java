package dev.scaraz.mars.core.domain;

import com.google.gson.Gson;
import dev.scaraz.mars.common.domain.AuditableEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.util.ClassUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "t_app_config")
public class AppConfig extends AuditableEntity {

    private static final Gson gson = new Gson();

    public enum Type {
        STRING,
        NUMBER,
        BOOLEAN,
        DATE,
        JSON
    }

    @Id
    private long id;

    @Column(updatable = false, unique = true)
    private String name;

    @Column
    private Type type;

    @Column(name = "class_type")
    private String classType;

    @Column
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String value;

    @Column
    private String description;

    public void setValue(String value) {
        type = Type.STRING;
        classType = String.class.getCanonicalName();
        this.value = value;
    }

    public void setAsBoolean(boolean value) {
        type = Type.BOOLEAN;
        classType = Boolean.class.getCanonicalName();
        this.value = String.valueOf(value);
    }

    public void setAsNumber(Number value) {
        type = Type.NUMBER;
        classType = value.getClass().getCanonicalName();
        this.value = value.toString();
    }

    public void setAsJson(Object value) {
        type = Type.JSON;
        classType = value.getClass().getCanonicalName();
        this.value = gson.toJson(value);
    }

    public void setAsDate(LocalDate localDate) {
        type = Type.DATE;
        classType = LocalDate.class.getCanonicalName();
        this.value = localDate.format(DateTimeFormatter.ISO_DATE);
    }

    public void setAsDateTime(LocalDateTime localDateTime) {
        type = Type.DATE;
        classType = LocalDateTime.class.getCanonicalName();
        this.value = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }


    public String getValue() {
        return value;
    }

    public Boolean getAsBoolean() {
        return Boolean.parseBoolean(value);
    }

    public Number getAsNumber() {
        try {
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

            throw new IllegalStateException("Unable to convert to number type");
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public Object getAsJson() {
        try {
            Class<?> aClass = getClass().getClassLoader().loadClass(classType);
            return gson.fromJson(value, aClass);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public LocalDate getAsDate() {
        if (type != Type.DATE) throw new IllegalStateException("Unable to convert value to Date");
        return LocalDate.parse(value);
    }

    public LocalDateTime getAsDateTime() {
        if (type != Type.DATE) throw new IllegalStateException("Unable to convert value to Date Time");
        return LocalDateTime.parse(value);
    }


    public boolean isNull() {
        return value == null;
    }

    public boolean isString() {
        return type == Type.STRING;
    }

    public boolean isBoolean() {
        return type == Type.BOOLEAN;
    }

    public boolean isNumber() {
        return type == Type.NUMBER;
    }

    public boolean isJson() {
        return type == Type.JSON;
    }
}
