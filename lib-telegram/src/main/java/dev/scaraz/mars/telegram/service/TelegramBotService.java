package dev.scaraz.mars.telegram.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.scaraz.mars.telegram.config.*;
import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramHandlerResult;
import dev.scaraz.mars.telegram.model.TelegramInterceptor;
import dev.scaraz.mars.telegram.model.TelegramProcessContext;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import dev.scaraz.mars.telegram.util.enums.ProcessCycle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.util.ClassUtils;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static dev.scaraz.mars.telegram.util.TelegramUtil.TELEGRAM_BOT_COMMAND_COMPARATOR;

/**
 * Telegram Bot Service.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Slf4j
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class TelegramBotService implements AutoCloseable {

    private static final Gson GSON = new GsonBuilder()
            .create();

    protected final LinkedList<TelegramProcessor> telegramProcessors = new LinkedList<>();

    @Autowired
    protected ConfigurableBeanFactory beanFactory;

    @Autowired
    protected TelegramArgumentMapper argumentMapper;

    @Autowired
    protected TelegramHandlerMapper handlerMapper;

    @Autowired
    protected TelegramInterceptorMapper interceptorMapper;

    /**
     * @return telegram api client implementation
     */
    public abstract DefaultAbsSender getClient();

    public void addProcessor(TelegramProcessor telegramProcessor) {
        log.info("Adding Telegram Processor {}", telegramProcessor.type());
        this.telegramProcessors.add(telegramProcessor);
    }

    public TelegramProcessor getProcessor(Update update) {
        for (int i = telegramProcessors.size() - 1; i >= 0; i--) {
            TelegramProcessor processor = telegramProcessors.get(i);
            if (processor.shouldProcess(update)) return processor;
        }
        return null;
    }

    protected void onUpdateReceived(Update update) {
        log.info("Telegram Update: {}", GSON.toJson(update));
        TelegramProcessor processor = getProcessor(update);

        try {
            if (processor == null) log.warn("No processor can handle current update {}", update.getUpdateId());
            else {
                InternalTelegram.init(TelegramProcessContext.builder()
                        .update(update)
                        .processor(processor)
                        .cycle(ProcessCycle.PROCESS)
                        .addAttribute(TelegramContextHolder.TG_USER, InternalTelegram.getUser(processor, update))
                        .addAttribute(TelegramContextHolder.TG_CHAT, InternalTelegram.getChat(processor, update))
                        .addAttribute(TelegramContextHolder.TG_CHAT_SOURCE, InternalTelegram.getChatSource(processor, update))
                        .build());

                try {
                    Optional<TelegramHandlerResult> processResult = processor.process(this, update);
                    if (processResult.isPresent()) {
                        TelegramHandlerResult result = processResult.get();

                        boolean shouldInvoke = interceptHandler(result);

                        if (shouldInvoke) {
                            invokeHandler(result.getHandler(), result.getArguments())
                                    .ifPresent(m -> InternalTelegram.update(b -> b.result(m)));
                        }
                    }
                }
                catch (Exception e) {
                    log.warn("Fail to process update {}", update.getUpdateId(), e);
                    processor.handleExceptions(this, update, e)
                            .ifPresent(m -> InternalTelegram.update(b -> b.result(m)));
                }
            }
        }
        catch (Exception ex) {
            log.error("Error On Update Received", ex);
            TelegramContextHolder.clear();
        }
    }

    private boolean interceptHandler(TelegramHandlerResult result) {
        List<TelegramInterceptor> interceptors = new ArrayList<>(interceptorMapper.getInterceptors());
        interceptors.sort(OrderComparator.INSTANCE);

        for (TelegramInterceptor interceptor : interceptors) {
            boolean doNext = interceptor.intercept(TelegramContextHolder.get().getType(), TelegramContextHolder.getUpdate(), result.getHandler());
            if (!doNext) return false;
        }
        return true;
    }

    private Optional<BotApiMethod<?>> invokeHandler(TelegramHandler commandHandler, Object[] arguments) throws Exception {
        InternalTelegram.update(b -> b.handler(commandHandler).handlerArguments(arguments));

        Method method = commandHandler.getMethod();
        Class<?> methodReturnType = method.getReturnType();
        log.debug("Derived method return type: {}", methodReturnType);
        log.debug("Bean {}", commandHandler.getBean());
        try {
            if (ClassUtils.isAssignable(Void.class, methodReturnType))
                method.invoke(commandHandler.getBean(), arguments);
            else if (ClassUtils.isAssignable(BotApiMethod.class, methodReturnType))
                return Optional.ofNullable((BotApiMethod<?>) method.invoke(commandHandler.getBean(), arguments));
            else log.error("Unsupported handler '{}'", commandHandler);
        }
        catch (InvocationTargetException ex) {
            if (ex.getCause() != null) throw (Exception) ex.getCause();
            throw ex;
        }
        return Optional.empty();
    }

    public Optional<BotApiMethod<?>> getHelpList(Update update, OptionalLong userKey) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(TelegramUtil.esc(buildHelpMessage(userKey)));
        return Optional.of(sendMessage);
    }

    public void registerStructuredReply(String name) {
        TelegramProcessContext context = TelegramContextHolder.get();
        HandlerType type = context.getType();
        Update update = context.getUpdate();

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

    @Override
    public void close() {
    }

}
