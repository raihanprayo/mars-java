package dev.scaraz.mars.common.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormDescriptor {
    boolean required() default false;

    boolean multiline() default false;

    String[] alias() default {};
}
