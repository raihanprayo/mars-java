@TypeDefs({
        @TypeDef(name = "dynamic-type", defaultForType = DynamicType.class, typeClass = DynamicSqlType.class)
})
@GenericGenerator(name = "uuid", strategy = "uuid2")
package dev.scaraz.mars.app.administration.domain.db;

import dev.scaraz.mars.common.domain.dynamic.DynamicSqlType;
import dev.scaraz.mars.common.domain.dynamic.DynamicType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;