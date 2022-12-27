package dev.scaraz.mars.telegram.service.processor;

import dev.scaraz.mars.telegram.TelegramArgumentResolver;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
public abstract class TelegramProcessor {

    @Autowired
    private TelegramArgumentResolver argumentResolver;

    public abstract HandlerType type();

    public abstract boolean shouldProcess(Update update);

    public abstract Optional<BotApiMethod<?>> process(TelegramBotService service, Update update);

    protected Object[] makeArgumentList(TelegramBotService service,
                                        TelegramHandler handler,
                                        Update update,
                                        @Nullable TelegramMessageCommand command) throws IllegalArgumentException {
        return argumentResolver.makeArgumentList(service, handler, update, type(), command);
    }

    protected <T> Optional<T> handleExceptions(Callable<Optional<T>> callable, Update update) {
        try {
            return callable.call();
        }
        catch (Exception e) {
            log.error("Could not process update: {}", update, e);
            return Optional.empty();
        }
    }

}