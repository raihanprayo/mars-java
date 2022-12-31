package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;


@FunctionalInterface
public interface TelegramArgResolver {
    List<HandlerType> DEFAULT_HANDLER_TYPE = List.of(HandlerType.ALL);

    Object resolve(MethodParameter mp,
                   TelegramHandlerContext ctx,
                   Update update,
                   @Nullable
                   TelegramMessageCommand mc);

    default List<HandlerType> handledFor() {
        return DEFAULT_HANDLER_TYPE;
    }

}
