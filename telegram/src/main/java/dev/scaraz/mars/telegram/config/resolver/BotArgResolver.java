package dev.scaraz.mars.telegram.config.resolver;

import dev.scaraz.mars.telegram.model.TelegramArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.model.TelegramTypeArgResolver;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import javax.annotation.Nullable;

@Component
public final class BotArgResolver implements TelegramTypeArgResolver<AbsSender> {
    @Override
    public AbsSender resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
        return ctx.getService().getClient();
    }
}
