package dev.scaraz.mars.telegram.config.processor;

import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramHandlers;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import dev.scaraz.mars.telegram.util.Util;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
        if (optionalCommandHandler.isPresent()) {
            TelegramHandler handler = optionalCommandHandler.get();
//            if (handler.getTelegramCommand().filter(TelegramCommand::isHelp).isPresent()) {
//                return bot.getHelpList(update, userKey);
//            }
            return bot.processHandler(handler, makeArgumentList(
                    bot,
                    handler,
                    update,
                    command
            ));
        }

        return Optional.empty();
    }

    @Override
    public Optional<BotApiMethod<?>> handleExceptions(TelegramBotService bot, Update update, Exception ex) {
        super.handleExceptions(bot, update, ex);
        long chatId = update.getMessage().getChatId();

        String message = TelegramUtil.exception(ex);
        return Optional.of(SendMessage.builder()
                .chatId(chatId)
                .text(message == null ? "<unknown>" : message)
                .parseMode(ParseMode.MARKDOWNV2)
                .build());
    }
}
