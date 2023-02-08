package dev.scaraz.mars.telegram.config.processor;

import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramHandlers;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.Util;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import static dev.scaraz.mars.telegram.util.TelegramUtil.KEY_LENGTH_COMPARATOR;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageProcessor extends TelegramProcessor {

    @Override
    public HandlerType type() {
        return HandlerType.MESSAGE;
    }

    @Override
    public boolean shouldProcess(Update update) {
        return update.hasMessage();
    }

    @Override
    public Optional<BotApiMethod<?>> process(TelegramBotService bot, Update update) throws Exception {
        TelegramMessageCommand command = new TelegramMessageCommand(update);
        Optional<TelegramHandler> optionalCommandHandler;
        OptionalLong userKey = Util.optionalOf(update.getMessage().getChatId());
        TelegramHandlers handlers = getHandlerMapper().getHandlers(userKey);

        if (command.getForwardedFrom().isPresent()) {
            optionalCommandHandler = Optional.ofNullable(
                    handlers.getForwardHandlerList().getOrDefault(command.getForwardedFrom().getAsLong(),
                            handlers.getDefaultForwardHandler()
                    )
            );
        }
        else {
            optionalCommandHandler = findCommandIgnoreCase(handlers, command);
            if (optionalCommandHandler.isEmpty()) {
                Optional<String> commandOpt = command.getCommand();
                if (commandOpt.isPresent()) {
                    String commandCommand = commandOpt.get();
                    optionalCommandHandler = handlers.getPatternCommandList().entrySet().stream()
                            .filter(entry -> commandCommand.startsWith(entry.getKey()))
                            .max(KEY_LENGTH_COMPARATOR)
                            .map(Map.Entry::getValue);
                }
                if (optionalCommandHandler.isEmpty()) {
                    log.debug("USING DEFAULT MESSAGE HANDLER");
                    optionalCommandHandler = Optional.ofNullable(handlers.getDefaultMessageHandler());
                }
            }
        }

        log.debug("Command handler: {}", optionalCommandHandler);
        if (optionalCommandHandler.isPresent()) {
            TelegramHandler handler = optionalCommandHandler.get();
            return bot.processHandler(handler, makeArgumentList(
                    bot,
                    handler,
                    update,
                    command
            ));
        }

        return Optional.empty();
    }

    private Optional<TelegramHandler> findCommandIgnoreCase(TelegramHandlers handlers, TelegramMessageCommand tgCommand) {
        if (tgCommand.isCommand()) {
            String inputCmd = tgCommand.getCommand().get();
            for (String cmd : handlers.getCommandList().keySet()) {
                if (inputCmd.equalsIgnoreCase(cmd)) return Optional.of(handlers.getCommandList().get(cmd));
            }
        }
        return Optional.empty();
    }

}
