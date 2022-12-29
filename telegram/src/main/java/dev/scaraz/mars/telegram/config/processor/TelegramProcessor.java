package dev.scaraz.mars.telegram.config.processor;

import dev.scaraz.mars.telegram.config.TelegramArgumentResolver;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public abstract class TelegramProcessor {

    @Setter
    @Getter
    private TelegramArgumentResolver argumentResolver;

    public abstract HandlerType type();

    public abstract boolean shouldProcess(Update update);

    public abstract Optional<BotApiMethod<?>> process(TelegramBotService bot, Update update);

    protected Object[] makeArgumentList(TelegramBotService service,
                                        TelegramHandler handler,
                                        Update update,
                                        @Nullable TelegramMessageCommand command) throws IllegalArgumentException {
        return getArgumentResolver().makeArgumentList(service, handler, update, type(), command);
    }

    protected <T> Optional<T> handleExceptions(Update update, Callable<Optional<T>> callable) {
        try {
            return callable.call();
        }
        catch (Exception e) {
            log.error("Could not process update: {}", update, e);
            return Optional.empty();
        }
    }

}