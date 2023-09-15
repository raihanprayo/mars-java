package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.annotation.context.ChatId;
import dev.scaraz.mars.telegram.model.TelegramAnnotationArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class ChatIdArgResolver implements TelegramAnnotationArgResolver {

    @Override
    public List<Class<? extends Annotation>> supportedAnnotations() {
        return List.of(ChatId.class);
    }

    @Override
    public Long resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        switch (ctx.getScope()) {
            case MESSAGE:
                return update.getMessage().getChatId();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }

    @Override
    public List<HandlerType> handledFor() {
        return List.of(HandlerType.MESSAGE, HandlerType.CALLBACK_QUERY);
    }
}
