package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.model.TelegramArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.model.TelegramTypeArgResolver;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.Nullable;

@Component
public final class UpdateArgResolver implements TelegramTypeArgResolver<Update> {
    @Override
    public Update resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        return update;
    }
}
