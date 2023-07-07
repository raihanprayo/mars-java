@TypeDefs({
        @TypeDef(name = "dynamic-type", defaultForType = DynamicType.class, typeClass = DynamicSqlType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
package dev.scaraz.mars.v1.admin.domain;

import dev.scaraz.mars.common.domain.dynamic.DynamicSqlType;
import dev.scaraz.mars.common.domain.dynamic.DynamicType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;