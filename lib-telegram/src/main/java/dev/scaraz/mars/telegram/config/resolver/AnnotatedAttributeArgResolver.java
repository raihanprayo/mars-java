package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.annotation.context.ChatId;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramAnnotationArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import jakarta.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

@Component
public class AnnotatedAttributeArgResolver implements TelegramAnnotationArgResolver {

    @Override
    public Object resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        if (mp.hasParameterAnnotation(ChatId.class))
            return ((Chat) TelegramContextHolder.getAttribute(TelegramContextHolder.TG_CHAT)).getId();
        if (mp.hasParameterAnnotation(UserId.class))
            return ((User) TelegramContextHolder.getAttribute(TelegramContextHolder.TG_USER)).getId();
        return null;
    }

    @Override
    public List<HandlerType> handledFor() {
        return TelegramAnnotationArgResolver.super.handledFor();
    }

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(ChatId.class, UserId.class);
    }

}
