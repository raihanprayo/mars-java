package dev.scaraz.mars.telegram.service;

import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.config.TelegramArgumentMapper;
import dev.scaraz.mars.telegram.config.TelegramHandlerMapper;
import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static dev.scaraz.mars.telegram.util.TelegramUtil.TELEGRAM_BOT_COMMAND_COMPARATOR;

/**
 * Telegram Bot Service.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class TelegramBotService implements AutoCloseable {
    protected final LinkedList<TelegramProcessor> telegramProcessors = new LinkedList<>();

    @Autowired
    protected ConfigurableBeanFactory beanFactory;

    @Autowired
    protected TelegramArgumentMapper argumentMapper;

    @Autowired
    protected TelegramHandlerMapper handlerMapper;

    /**
     * @return telegram api client implementation
     */
    public abstract DefaultAbsSender getClient();

    public void addProcessor(TelegramProcessor telegramProcessor) {
        log.info("Adding Telegram Processor {}", telegramProcessor.type());
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

    @Autowired
    private void initialize(List<TelegramProcessor> telegramProcessors) {
        telegramProcessors.forEach(this::addProcessor);
    }

    private String buildHelpMessage(OptionalLong userKey) {
        StringBuilder sb = new StringBuilder();
        String prefixHelpMessage = handlerMapper.getHandlers(userKey).getPrefixHelpMessage();
        if (prefixHelpMessage != null) {
            sb.append(prefixHelpMessage);
        }
        handlerMapper.getCommandList(userKey)
                .sorted(TELEGRAM_BOT_COMMAND_COMPARATOR)
                .forEach(method -> sb
                        .append(method.getCommand())
                        .append(' ')
                        .append(beanFactory.resolveEmbeddedValue(method.getDescription()))
                        .append('\n')
                );
        return sb.toString();
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

}
