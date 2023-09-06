@TypeDefs({
        @TypeDef(name = "dynamic-type", defaultForType = DynamicType.class, typeClass = DynamicSqlType.class)
//        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
//        @TypeDef(name = "long-arr", typeClass = LongArrayType.class)
})
@GenericGenerator(name = "uuid", strategy = "uuid2")
package dev.scaraz.mars.app.api.domain;

import dev.scaraz.mars.common.domain.dynamic.DynamicSqlType;
import dev.scaraz.mars.common.domain.dynamic.DynamicType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;