package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.model.TelegramAnnotationArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public final class UserIdArgResolver implements TelegramAnnotationArgResolver {

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(UserId.class);
    }

    @Override
    public Long resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        switch (ctx.getScope()) {
            case MESSAGE:
                return update.getMessage().getFrom().getId();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }
}
