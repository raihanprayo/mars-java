package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.TelegramArgumentResolver;
import dev.scaraz.mars.telegram.model.*;
import dev.scaraz.mars.telegram.service.processor.TelegramProcessor;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.extern.slf4j.Slf4j;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.TelegramForward;
import dev.scaraz.mars.telegram.annotation.TelegramHelp;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;
import dev.scaraz.mars.telegram.util.Util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
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

    private final TelegramBotsApi api;
    private final EmbeddedValueResolver embeddedValueResolver;

    @Autowired
    protected TelegramArgumentResolver argumentResolver;
    @Autowired
    protected List<? extends TelegramProcessor> telegramProcessors;

    public TelegramBotService(TelegramBotsApi api, ConfigurableBeanFactory configurableBeanFactory) {
        this.api = api;
        embeddedValueResolver = new EmbeddedValueResolver(configurableBeanFactory);

//        BiFunction<TelegramMessageCommand, Update, Long> messageUserIdExtractor = (telegramMessageCommand, update) ->
//                update.getMessage().getFrom().getId();
//
//        Function<Update, Long> callbackQueryUserIdExtractor = update ->
//                update.getCallbackQuery().getFrom().getId();
//        messageArgumentMapper = ImmutableMap.<Type, BiFunction<TelegramMessageCommand, Update, ?>>builder()
//                .put(Update.class, (telegramMessageCommand, update) -> update)
//                .put(TelegramBotsApi.class, (telegramMessageCommand, update) -> api)
//                .put(TelegramBotService.class, (telegramMessageCommand, update) -> this)
//                .put(Message.class, (telegramMessageCommand, update) -> update.getMessage())
//                .put(DefaultAbsSender.class, (telegramMessageCommand, update) -> getClient())
//                .put(User.class, (telegramMessageCommand, update) -> update.getMessage().getFrom())
//
//                .put(TelegramMessageCommand.class, (telegramMessageCommand, update) -> telegramMessageCommand)
//                .put(String.class, (telegramMessageCommand, update) -> telegramMessageCommand.getArgument().orElse(null))
//                .put(long.class, messageUserIdExtractor)
//                .put(Long.class, messageUserIdExtractor)
//                .put(Instant.class, ((telegramMessageCommand, update) -> {
//                    Message message = update.getMessage();
//                    return Instant.ofEpochSecond(Optional.ofNullable(message.getForwardDate()).orElse(message.getDate()));
//                }))
//                .build();
//
//        callbackQueryArgumentMapper = ImmutableMap.<Type, Function<Update, ?>>builder()
//                .put(Update.class, update -> update)
//                .put(TelegramBotsApi.class, update -> api)
//                .put(TelegramBotService.class, update -> this)
//                .put(DefaultAbsSender.class, update -> getClient())
//                .put(Message.class, update -> update.getCallbackQuery().getMessage())
//                .put(User.class, update -> update.getCallbackQuery().getFrom())
//
//                .put(String.class, update -> update.getCallbackQuery().getData())
//                .put(CallbackQuery.class, Update::getCallbackQuery)
//                .put(long.class, callbackQueryUserIdExtractor)
//                .put(Long.class, callbackQueryUserIdExtractor)
//                .put(CallbackQueryId.class, update -> new CallbackQueryId(update.getCallbackQuery().getId()))
//                .build();
    }

    /**
     * @return telegram api client implementation
     */
    public abstract DefaultAbsSender getClient();


    /**
     * Main dispatcher method which takes {@link Update} object and calls controller method to process update.
     */
    @SuppressWarnings("WeakerAccess")
    public Optional<BotApiMethod<?>> updateProcess(Update update) {
        log.debug("Update {} received", update);
//        return processors.stream()
//                .filter(processorDescriptor -> processorDescriptor.test(update))
//                .findFirst()
//                .flatMap(processorDescriptor -> processorDescriptor.apply(update));
        return telegramProcessors.stream()
                .filter(processor -> processor.shouldProcess(update))
                .findFirst()
                .flatMap(processor -> processor.process(this, update));
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
                        .append(embeddedValueResolver.resolveStringValue(method.getDescription()))
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
                    String parsedFromStr = embeddedValueResolver.resolveStringValue(from);
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
