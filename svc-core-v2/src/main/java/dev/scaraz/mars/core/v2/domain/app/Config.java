package dev.scaraz.mars.core.v2.domain.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.scaraz.mars.common.domain.AuditableEntity;
import dev.scaraz.mars.core.v2.util.dynamic.DynamicValue;
import dev.scaraz.mars.core.v2.util.enums.DynamicType;
import dev.scaraz.mars.core.v2.util.hibernate.DynamicSqlType;
import dev.scaraz.mars.core.v2.util.lambda.DynamicValueDeserializer;
import dev.scaraz.mars.core.v2.util.lambda.DynamicValueSerializer;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Getter
@Setter
@ToString

@Entity
@Table(name = "t_config")
@TypeDef(name = "dynamic-type",
        defaultForType = DynamicType.class,
        typeClass = DynamicSqlType.class)
public class Config extends AuditableEntity implements DynamicValue {

    @Id
    @Column(name = "id")
    private String key;

    @ManyToOne
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
            else
                this.value = value.toString();
        }
    }

}
