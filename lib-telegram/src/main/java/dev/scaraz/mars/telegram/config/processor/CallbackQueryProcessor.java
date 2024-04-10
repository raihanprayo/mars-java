package dev.scaraz.mars.telegram.config.processor;

import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramHandlerResult;
import dev.scaraz.mars.telegram.model.TelegramHandlers;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.Util;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CallbackQueryProcessor extends TelegramProcessor {

    private final AntPathMatcher pathMatcher = new AntPathMatcher(":");

    @Override
    public HandlerType type() {
        return HandlerType.CALLBACK_QUERY;
    }

    @Override
    public boolean shouldProcess(Update update) {
        return update.hasCallbackQuery();
    }

    @Override
    public Optional<TelegramHandlerResult> process(TelegramBotService bot, Update update) throws Exception {
        TelegramHandlers handlers = getHandlerMapper().getHandlers(Util.optionalOf(update.getCallbackQuery().getFrom().getId()));

        TelegramHandler handler = findSpecificHandler(handlers, update);

        return Optional.ofNullable(handler)
                .map(h -> new TelegramHandlerResult(
                        handler,
                        makeArgumentList(bot, handler, update, null)
                ));
    }

    private TelegramHandler findSpecificHandler(TelegramHandlers handlers, Update update) {
        String data = update.getCallbackQuery().getData();
        for (String path : handlers.getCallbackQueryList().keySet()) {
            if (pathMatcher.match(path, data))
                return handlers.getCallbackQueryList().get(path);
        }
        return handlers.getDefaultCallbackQueryHandler();
    }

}
