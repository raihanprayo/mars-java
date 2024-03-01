package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.model.TelegramTypeArgResolver;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;

public class ChatSourceArgResolver implements TelegramTypeArgResolver<ChatSource> {

    @Override
    public ChatSource resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        return TelegramContextHolder.getChatSource();
    }

}
