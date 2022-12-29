package dev.scaraz.mars.telegram.config.processor;

import dev.scaraz.mars.telegram.annotation.TelegramCommand;
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
    public Optional<BotApiMethod<?>> process(TelegramBotService bot, Update update) {
        TelegramMessageCommand command = new TelegramMessageCommand(update);
        Optional<TelegramHandler> optionalCommandHandler;
        OptionalLong userKey = Util.optionalOf(update.getMessage().getChatId());
        TelegramHandlers handlers = bot.getHandlers(userKey);

        if (command.getForwardedFrom().isPresent()) {
            optionalCommandHandler = Optional.ofNullable(
                    handlers.getForwardHandlerList().getOrDefault(command.getForwardedFrom().getAsLong(),
                            handlers.getDefaultForwardHandler()
                    )
            );
        }
        else {
            Optional<String> commandCommandOpt = command.getCommand();
            optionalCommandHandler = commandCommandOpt.map(cmd -> handlers.getCommandList().get(cmd));
            if (optionalCommandHandler.isEmpty()) {
                if (commandCommandOpt.isPresent()) {
                    String commandCommand = commandCommandOpt.get();
                    optionalCommandHandler = handlers.getPatternCommandList().entrySet().stream()
                            .filter(entry -> commandCommand.startsWith(entry.getKey()))
                            .max(KEY_LENGTH_COMPARATOR)
                            .map(Map.Entry::getValue);
                }
                if (optionalCommandHandler.isEmpty()) {
                    optionalCommandHandler = Optional.ofNullable(handlers.getDefaultMessageHandler());
                }
            }
        }

        log.debug("Command handler: {}", optionalCommandHandler);

        return optionalCommandHandler.flatMap(commandHandler -> handleExceptions(update, () -> {
            if (commandHandler.getTelegramCommand().filter(TelegramCommand::isHelp).isPresent()) {
                bot.sendHelpList(update, userKey);
                return Optional.empty();
            }
            return bot.processHandler(commandHandler, makeArgumentList(
                    bot,
                    commandHandler,
                    update,
                    command
            ));
        }));
    }

}
