package dev.scaraz.mars.common.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.domain.dynamic.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDTO implements DynamicValue {

    private String key;

    @Setter(AccessLevel.NONE)
    private String value;

    private String tag;

    @JsonDeserialize(using = DynamicJsonDeserializer.class)
    @JsonSerialize(using = DynamicJsonSerializer.class)
    private DynamicType type;

    private String description;

    @Override
    public String toString() {
        return "ConfigDTO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", tag='" + tag + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public void setValue(Object value) {
        if (value == null) this.value = null;
        else {
            if (value instanceof DynamicValue) {
                this.value = ((DynamicValue) value).getValue();
            }
            else {
                if (this.type == null) {
                    this.type = DynamicType.of(value.getClass());
                }
                else {
                    boolean isAppliceable = this.type.isAssignable(value.getClass());
                    if (!isAppliceable)
                        throw new IllegalArgumentException("Cannot set value from existing type " + type);
                }

                if (type == DynamicType.BOOLEAN)
                    this.value = DynamicValueSerializer.BOOL.get((Boolean) value);
                else if (type == DynamicType.LIST)
                    this.value = DynamicValueSerializer.LIST_STRING.get((List<?>) value);
                else
                    this.value = value.toString();
            }
        }
    }
}
