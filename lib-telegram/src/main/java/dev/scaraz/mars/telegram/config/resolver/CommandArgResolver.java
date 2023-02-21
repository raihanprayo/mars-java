package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.annotation.context.Command;
import dev.scaraz.mars.telegram.model.TelegramAnnotationArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

@Component
public final class CommandArgResolver implements TelegramAnnotationArgResolver {
    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(Command.class);
    }

    @Override
    public List<HandlerType> handledFor() {
        return List.of(HandlerType.MESSAGE);
    }

    @Override
    public Object resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        return mc.getCommand().orElse(null);
    }
}
