@TypeDefs({
        @TypeDef(name = "dynamic-type", defaultForType = DynamicType.class, typeClass = DynamicSqlType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
package dev.scaraz.mars.core.v2.domain;

import dev.scaraz.mars.core.v2.util.enums.DynamicType;
import dev.scaraz.mars.core.v2.util.hibernate.DynamicSqlType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;