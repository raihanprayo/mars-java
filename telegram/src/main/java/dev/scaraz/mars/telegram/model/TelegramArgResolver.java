package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


@FunctionalInterface
public interface TelegramArgResolver<T> {

    Object resolve(
            TelegramHandlerContext context,
            Update update,
            @Nullable TelegramMessageCommand messageCommand);

    default HandlerType handledFor() {
        return HandlerType.BOTH;
    }

    default Type getType() {
        ParameterizedType p = (ParameterizedType) TelegramArgResolver.this.getClass()
                .getGenericSuperclass();
        return p.getActualTypeArguments()[0];
    }
}
