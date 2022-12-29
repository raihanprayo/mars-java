package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.config.TelegramArgumentResolver;
import dev.scaraz.mars.telegram.model.*;
import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import lombok.extern.slf4j.Slf4j;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.TelegramForward;
import dev.scaraz.mars.telegram.annotation.TelegramHelp;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static dev.scaraz.mars.telegram.util.TelegramUtil.TELEGRAM_BOT_COMMAND_COMPARATOR;

/**
 * Telegram Bot Service.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class TelegramBotService implements AutoCloseable {

    private final Map<OptionalLong, TelegramHandlers> handlers = new HashMap<>();
    protected final LinkedList<TelegramProcessor> telegramProcessors = new LinkedList<>();

    private final EmbeddedValueResolver valueResolver;

    @Autowired
    protected TelegramArgumentResolver argumentResolver;

    public TelegramBotService(EmbeddedValueResolver valueResolver) {
        this.valueResolver = valueResolver;
    }

    /**
     * @return telegram api client implementation
     */
    public abstract DefaultAbsSender getClient();

    public void addProcessor(TelegramProcessor telegramProcessor) {
        log.info("Adding Telegram Processor {}", telegramProcessor.type());
        telegramProcessor.setArgumentResolver(argumentResolver);
        this.telegramProcessors.add(telegramProcessor);
    }

    /**
     * Main dispatcher method which takes {@link Update} object and calls controller method to process update.
     */
    @SuppressWarnings("WeakerAccess")
    public Optional<BotApiMethod<?>> updateProcess(Update update) {
        log.debug("Update {} received", update);

        for (int i = telegramProcessors.size() - 1; i >= 0; i--) {
            TelegramProcessor processor = telegramProcessors.get(i);
            if (processor.shouldProcess(update)) return processor.process(this, update);
        }

        log.warn("No processor can handle current update");
        return Optional.empty();
    }

    public Optional<BotApiMethod<?>> processHandler(TelegramHandler commandHandler, Object[] arguments) throws IllegalAccessException, InvocationTargetException {
        Method method = commandHandler.getMethod();
        Class<?> methodReturnType = method.getReturnType();
        log.debug("Derived method return type: {}", methodReturnType);
        if (methodReturnType == void.class || methodReturnType == Void.class) {
            method.invoke(commandHandler.getBean(), arguments);
        }
        else if (BotApiMethod.class.isAssignableFrom(methodReturnType)) {
            return Optional.ofNullable((BotApiMethod<?>) method.invoke(commandHandler.getBean(), arguments));
        }
        else {
            log.error("Unsupported handler '{}'", commandHandler);
        }
        return Optional.empty();
    }

    public void sendHelpList(Update update, OptionalLong userKey) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(buildHelpMessage(userKey));
        getClient().execute(sendMessage);
    }

    private String buildHelpMessage(OptionalLong userKey) {
        StringBuilder sb = new StringBuilder();
        String prefixHelpMessage = getHandlers(userKey).getPrefixHelpMessage();
        if (prefixHelpMessage != null) {
            sb.append(prefixHelpMessage);
        }
        getCommandList(userKey)
                .sorted(TELEGRAM_BOT_COMMAND_COMPARATOR)
                .forEach(method -> sb
                        .append(method.getCommand())
                        .append(' ')
                        .append(valueResolver.resolveStringValue(method.getDescription()))
                        .append('\n')
                );
        return sb.toString();
    }

    /**
     * Enumerates all visible command handlers for given user.
     */
    @SuppressWarnings("WeakerAccess")
    public Stream<TelegramBotCommand> getCommandList(OptionalLong userKey) {
        return Stream.concat(
                getHandlers(userKey).getCommandList().entrySet().stream()
                        .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
                        .map(entry -> new TelegramBotCommand(
                                entry.getKey(),
                                entry.getValue().getTelegramCommand().map(TelegramCommand::description).orElse("")
                        )),
                getHandlers(userKey).getPatternCommandList().entrySet().stream()
                        .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
                        .map(entry -> new TelegramBotCommand(
                                entry.getKey(),
                                entry.getValue().getTelegramCommand().map(TelegramCommand::description).orElse("")
                        ))
        );
    }

//    private Object[] makeArgumentList() {
//        return new Object[]{};
//    }
//    private Object[] makeMessageArgumentList(Method method, TelegramMessageCommand telegramMessageCommand, Update update) {
//        return Arrays.stream(method.getGenericParameterTypes())
//                .map(type -> messageArgumentMapper.getOrDefault(type, (t, u) -> null))
//                .map(mapper -> mapper.apply(telegramMessageCommand, update))
//                .toArray();
//    }
//    private Object[] makeCallbackQueryArgumentList(Method method, Update update) {
//        return Arrays.stream(method.getGenericParameterTypes())
//                .map(type -> callbackQueryArgumentMapper.getOrDefault(type, u -> null))
//                .map(mapper -> mapper.apply(update))
//                .toArray();
//    }

    /**
     * Add {@link TelegramCommand} handler.
     */
    @SuppressWarnings("WeakerAccess")
    public void addHandler(Object bean, Method method, OptionalLong userId) {
        TelegramCommand command = AnnotatedElementUtils.findMergedAnnotation(method, TelegramCommand.class);
        if (command != null) {
            for (String cmd : command.commands()) {
                TelegramHandler telegramHandler = new TelegramHandler(bean, method, command);
//                if (cmd.endsWith(patternCommandSuffix)) {
//                    createOrGet(userId).getPatternCommandList()
//                            .put(cmd.substring(0, cmd.length() - patternCommandSuffix.length()), telegramHandler);
//                }
//                else {
//                }
                createHandlers(userId).getCommandList().put(cmd, telegramHandler);
            }
        }
    }

    /**
     * Add {@link TelegramMessage} handler.
     */
    @SuppressWarnings("WeakerAccess")
    public void addDefaultMessageHandler(Object bean, Method method, OptionalLong userId) {
        createHandlers(userId).setDefaultMessageHandler(new TelegramHandler(bean, method, null));
    }

    /**
     * Add {@link TelegramCallbackQuery} handler.
     */
    @SuppressWarnings("WeakerAccess")
    public void addDefaultCallbackQueryHandler(Object bean, Method method, OptionalLong userId) {
        createHandlers(userId).setDefaultCallbackQueryHandler(new TelegramHandler(bean, method, null));
    }

    /**
     * Add {@link TelegramForward} handler.
     */
    @SuppressWarnings("WeakerAccess")
    public void addForwardMessageHandler(Object bean, Method method, OptionalLong userId) {
        TelegramForward forward = AnnotatedElementUtils.findMergedAnnotation(method, TelegramForward.class);
        if (forward != null) {
            String[] fromArr = forward.from();
            if (fromArr.length == 0) {
                createHandlers(userId).setDefaultForwardHandler(new TelegramHandler(bean, method, null));
            }
            else {
                for (String from : fromArr) {
                    String parsedFromStr = valueResolver.resolveStringValue(from);
                    if (parsedFromStr == null) {
                        throw new RuntimeException("NPE in " + from);
                    }
                    for (String fromValue : parsedFromStr.split(",")) {
                        Long parsedFrom = Long.valueOf(fromValue);
                        createHandlers(userId).getForwardHandlerList()
                                .put(parsedFrom, new TelegramHandler(bean, method, null));
                    }
                }
            }
        }
    }

    /**
     * Add help method for {@code userKey}.
     */
    @SuppressWarnings("WeakerAccess")
    public void addHelpMethod(OptionalLong userKey) {
        try {
            Method helpMethod = getClass().getMethod("helpMethod");
            TelegramCommand command = AnnotatedElementUtils.findMergedAnnotation(helpMethod, TelegramCommand.class);
            if (command != null) {
                for (String cmd : command.commands()) {
                    createHandlers(userKey).getCommandList()
                            .put(cmd, new TelegramHandler(this, helpMethod, command));
                }
            }
        }
        catch (Exception e) {
            log.error("Could not add help method", e);
        }
    }

    /**
     * Default help method.
     */
    @SuppressWarnings("WeakerAccess")
    @TelegramCommand(
            commands = "/help",
            isHelp = true,
            description = "#{@loc?.t('TelegramBotService.HELP.DESC')?:'This help'}"
    )
    public void helpMethod() {
    }

    @Override
    public void close() {
    }

    /**
     * Handler for {@link TelegramHelp} method.
     */
    @SuppressWarnings("WeakerAccess")
    public void addHelpPrefixMethod(Object bean, Method method, OptionalLong userId) {
        try {
            createHandlers(userId).setPrefixHelpMessage(method.invoke(bean).toString());
        }
        catch (Exception e) {
            log.error("Can not get help prefix", e);
        }
    }

    public TelegramHandlers getHandlers(OptionalLong key) {
        if (!handlers.containsKey(key))
            return handlers.get(OptionalLong.empty());

        return handlers.get(key);
    }

    public TelegramHandlers createHandlers(OptionalLong key) {
        return handlers.computeIfAbsent(key, k -> new TelegramHandlers());
    }

}
