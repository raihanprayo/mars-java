package dev.scaraz.mars.app.administration.config.telegram;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrationRestriction {
}
