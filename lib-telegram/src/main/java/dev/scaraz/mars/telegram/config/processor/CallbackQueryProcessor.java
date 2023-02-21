package dev.scaraz.mars.telegram.config.processor;

import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramHandlers;
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
    public Optional<BotApiMethod<?>> process(TelegramBotService bot, Update update) throws Exception {
        TelegramHandlers handlers = getHandlerMapper().getHandlers(Util.optionalOf(update.getCallbackQuery().getFrom().getId()));

        TelegramHandler handler = findSpecificHandler(handlers, update);

        return handler == null ?
                Optional.empty() :
                bot.processHandler(handler, makeArgumentList(bot, handler, update, null));
    }

    private TelegramHandler findSpecificHandler(TelegramHandlers handlers, Update update) {
        String data = update.getCallbackQuery().getData();
        return Optional.ofNullable(handlers.getCallbackQueryList().get(data))
                .orElseGet(handlers::getDefaultCallbackQueryHandler);
    }

}
