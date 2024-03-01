package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.util.enums.HandlerType;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramInterceptor {

    boolean intercept(HandlerType type, Update update, TelegramHandler handler);

}
