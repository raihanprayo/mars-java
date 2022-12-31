package dev.scaraz.mars.telegram.config.processor;

import dev.scaraz.mars.telegram.config.TelegramArgumentMapper;
import dev.scaraz.mars.telegram.config.TelegramHandlerMapper;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
public abstract class TelegramProcessor {

    @Getter
    @Autowired
    private TelegramArgumentMapper argumentMapper;

    @Getter
    @Autowired
    private TelegramHandlerMapper handlerMapper;

    public abstract HandlerType type();

    public abstract boolean shouldProcess(Update update);

    public abstract Optional<BotApiMethod<?>> process(TelegramBotService bot, Update update) throws Exception;

    public Optional<BotApiMethod<?>> handleExceptions(TelegramBotService bot, Update update, Exception ex) {
        ex.printStackTrace();
        log.error("Could not process update: {}", update.getUpdateId());
        log.error(ex.getMessage());
        return Optional.empty();
    }

    protected Object[] makeArgumentList(TelegramBotService service,
                                        TelegramHandler handler,
                                        Update update,
                                        @Nullable TelegramMessageCommand command) throws IllegalArgumentException {
        return getArgumentMapper().makeArgumentList(service, handler, update, type(), command);
    }

}