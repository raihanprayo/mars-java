package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.model.TelegramTypeArgResolver;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import jakarta.annotation.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public final class MessageArgResolver implements TelegramTypeArgResolver<Message> {

    @Override
    public List<HandlerType> handledFor() {
        return List.of(HandlerType.MESSAGE);
    }

    @Override
    public Message resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        return update.getMessage();
    }
}
