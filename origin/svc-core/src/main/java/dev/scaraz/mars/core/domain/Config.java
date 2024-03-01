package dev.scaraz.mars.core.domain;

import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.common.domain.dynamic.DynamicType;
import dev.scaraz.mars.common.domain.dynamic.DynamicValue;
import dev.scaraz.mars.common.domain.dynamic.DynamicValueSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString

@Entity
@Table(name = "t_config")
public class Config extends AuditableEntity implements DynamicValue {

    @Id
    @Column(name = "id")
    private String key;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id")
    private ConfigTag tag;

    @Column
    @Type(type = "dynamic-type")
    private DynamicType type;

    @Column
    @Setter(AccessLevel.NONE)
    private String value;

    @Column
    private String description;

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
