package dev.scaraz.mars.telegram.service.resolver;

import dev.scaraz.mars.telegram.model.TelegramArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;

public class MessageArgResolver implements TelegramArgResolver<Message> {

    @Override
    public Object resolve(TelegramHandlerContext context, Update update, @Nullable TelegramMessageCommand messageCommand) {
        if (context.getScope() == HandlerType.CALLBACK_QUERY) return update.getCallbackQuery().getMessage();
        return update.getMessage();
    }

}
