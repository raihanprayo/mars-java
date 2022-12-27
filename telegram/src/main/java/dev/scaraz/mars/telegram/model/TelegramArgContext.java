package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.lang.reflect.Method;
import java.util.Optional;

@Getter
@Builder
@RequiredArgsConstructor
public class TelegramArgContext {

    private final HandlerType scope;

    private final TelegramBotsApi api;
    private final TelegramBotService service;

    @Getter(AccessLevel.NONE)
    private final TelegramCommand command;

    public Optional<TelegramCommand> getCommand() {
        return Optional.ofNullable(command);
    }
}
