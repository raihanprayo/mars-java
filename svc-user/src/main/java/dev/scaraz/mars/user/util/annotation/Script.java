package dev.scaraz.mars.user.util.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Script {
    @AliasFor(annotation = Component.class, attribute = "value")
    String name() default "";
    boolean repeatable() default false;
}
