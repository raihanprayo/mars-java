package dev.scaraz.mars.telegram.model;

import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.Nullable;
import java.lang.reflect.Method;

public interface TelegramTypeArgResolver<T> extends TelegramArgResolver {

    @Override
    T resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc);

    default Class<T> getType() {
        try {
            Method method = this.getClass().getDeclaredMethod("resolve", MethodParameter.class, TelegramHandlerContext.class, Update.class, TelegramMessageCommand.class);
            return (Class<T>) method.getReturnType();
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

}
