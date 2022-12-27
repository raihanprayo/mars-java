package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.Builder;
import lombok.Getter;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.lang.reflect.Method;

@Getter
public class TelegramHandlerContext extends TelegramHandler {

    private final TelegramBotsApi api;
    private final TelegramBotService service;
    private final HandlerType scope;

    @Builder
    public TelegramHandlerContext(HandlerType scope,
                                  Object bean,
                                  Method method,
                                  TelegramBotsApi api,
                                  TelegramBotService service,
                                  TelegramCommand telegramCommand) {
        super(bean, method, telegramCommand);
        this.scope = scope;
        this.api = api;
        this.service = service;
    }

}
