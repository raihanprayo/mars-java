package dev.scaraz.mars.telegram.annotation;

import dev.scaraz.mars.telegram.TelegramApiConfiguration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable Telegram Bot annotations processing.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(TelegramApiConfiguration.class)
@Documented
@Inherited
public @interface EnableTelegramBot {
}
