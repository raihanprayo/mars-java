package dev.scaraz.mars.telegram.service.processor;

import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.Util;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Component
public class CallbackQueryProcessor extends TelegramProcessor {
    @Override
    public HandlerType type() {
        return HandlerType.CALLBACK_QUERY;
    }

    @Override
    public boolean shouldProcess(Update update) {
        return update.hasCallbackQuery();
    }

    @Override
    public Optional<BotApiMethod<?>> process(TelegramBotService service, Update update) {
        return Optional.ofNullable(
                service.getHandlers(Util.optionalOf(update
                        .getCallbackQuery()
                        .getFrom()
                        .getId())
                ).getDefaultCallbackQueryHandler()
        ).flatMap(handler -> handleExceptions(
                () -> service.processHandler(handler, makeArgumentList(
                        service,
                        handler,
                        update,
                        null
                )),
                update
        ));
    }
}
