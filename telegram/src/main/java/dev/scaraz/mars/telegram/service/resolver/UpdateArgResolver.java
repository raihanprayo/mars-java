package dev.scaraz.mars.telegram.service.resolver;

import dev.scaraz.mars.telegram.model.TelegramArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;

public class UpdateArgResolver implements TelegramArgResolver<Update> {
    @Override
    public Object resolve(TelegramHandlerContext context, Update update, @Nullable TelegramMessageCommand messageCommand) {
        return update;
    }
}
