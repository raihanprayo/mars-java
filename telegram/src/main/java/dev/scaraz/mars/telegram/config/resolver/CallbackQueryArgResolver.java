package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.model.TelegramArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.model.TelegramTypeArgResolver;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.List;

@Component
public final class CallbackQueryArgResolver implements TelegramTypeArgResolver<CallbackQuery> {
    @Override
    public List<HandlerType> handledFor() {
        return List.of(HandlerType.CALLBACK_QUERY);
    }

    @Override
    public CallbackQuery resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        return update.getCallbackQuery();
    }
}
