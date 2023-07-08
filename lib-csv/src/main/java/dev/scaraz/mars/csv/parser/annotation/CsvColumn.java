package dev.scaraz.mars.csv.parser.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvColumn {
    String[] value() default {};
}
