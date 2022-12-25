package dev.scaraz.mars.common.tools.annotation.telegram;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TgCommand {

    @AliasFor("command")
    String value() default "";

    @AliasFor("value")
    String command() default "";

}
