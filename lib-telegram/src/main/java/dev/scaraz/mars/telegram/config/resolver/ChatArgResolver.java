package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.model.TelegramArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.Nullable;
import java.util.List;

@Component
public class ChatArgResolver implements TelegramArgResolver {

    @Override
    public Chat resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        switch (ctx.getScope()) {
            case MESSAGE:
                return update.getMessage().getChat();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getMessage().getChat();
        }

        return null;
    }

    @Override
    public List<HandlerType> handledFor() {
        return TelegramArgResolver.super.handledFor();
    }
}
