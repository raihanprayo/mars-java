package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.TelegramForward;
import dev.scaraz.mars.telegram.annotation.TelegramHelp;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;

import dev.scaraz.mars.telegram.model.TelegramBotCommand;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramHandlers;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Bean post-processor to process Telegram Bot API annotations
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Slf4j
@RequiredArgsConstructor

@Priority(10)
@Component
public class TelegramHandlerMapper implements BeanPostProcessor {
    private final ConfigurableBeanFactory beanFactory;
    private final Map<String, Class<?>> botControllerMap = new HashMap<>();
    private final Map<OptionalLong, Map<String, Class<?>>> botControllerMapByUserId = new HashMap<>();
    private final Map<OptionalLong, TelegramHandlers> handlers = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        TelegramBot telegramBot = AnnotatedElementUtils.findMergedAnnotation(beanClass, TelegramBot.class);
        if (telegramBot != null) {
            if (telegramBot.userId().length != 0) {
                for (String userId : telegramBot.userId()) {
                    String evalUserId = beanFactory.resolveEmbeddedValue(userId);
                    if (evalUserId == null) {
                        throw new RuntimeException("NPE at beanClass: " + beanClass + " on userId: " + userId);
                    }
                    for (String evaluatedUserId : evalUserId.split(",")) {
                        log.info("Bind TelegramBot controller: {} for userId: {}", beanClass, userId);
                        botControllerMapByUserId.computeIfAbsent(OptionalLong.of(Long.parseLong(evaluatedUserId)), key -> new HashMap<>())
                                .put(beanName, beanClass);
                    }
                }
            }
            else {
                log.info("Bind TelegramBot controller: {}", beanClass);
                botControllerMap.put(beanName, beanClass);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, String beanName) throws BeansException {
        bindHandler(bean, beanName, botControllerMap.get(beanName), OptionalLong.empty());
        botControllerMapByUserId.forEach((userId, original) ->
                bindHandler(bean, beanName, original.get(beanName), userId)
        );
        return bean;
    }

    /**
     * Enumerates all visible command handlers for given user.
     */
    public Stream<TelegramBotCommand> getCommandList(OptionalLong userId) {
        return Stream.concat(
                getHandlers(userId).getCommandList().entrySet().stream()
                        .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
                        .map(entry -> new TelegramBotCommand(
                                entry.getKey(),
                                entry.getValue().getTelegramCommand().map(TelegramCommand::description).orElse("")
                        )),
                getHandlers(userId).getPatternCommandList().entrySet().stream()
                        .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
                        .map(entry -> new TelegramBotCommand(
                                entry.getKey(),
                                entry.getValue().getTelegramCommand().map(TelegramCommand::description).orElse("")
                        ))
        );
    }

    public TelegramHandlers getHandlers(OptionalLong userId) {
        if (handlers.containsKey(userId)) return handlers.get(userId);
        return handlers.get(OptionalLong.empty());
    }

    private void addHandlers(OptionalLong key, Consumer<TelegramHandlers> handlersConsumer) {
        handlersConsumer.accept(handlers.computeIfAbsent(key, k -> new TelegramHandlers()));
    }

    private void bindHandler(@NonNull Object bean, String beanName, Class<?> original, OptionalLong userId) {
        if (original != null) {
            log.debug("Processing class {} as bean {} for user {}",
                    bean.getClass(), beanName, userId
            );
            for (Method method : original.getMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramCommand.class)) {
                    log.debug("Found method {}", method.getName());
                    bindCommandHandler(bean, method, userId);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramMessage.class)) {
                    log.debug("Found method {}", method.getName());
                    bindDefaultMessageHandler(bean, method, userId);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramForward.class)) {
                    log.debug("Found method {}", method.getName());
                    bindForwardHandler(bean, method, userId);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramHelp.class)) {
                    log.debug("Found method {}", method.getName());
                    bindHelpPrefix(bean, method, userId);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramCallbackQuery.class)) {
                    log.debug("Found method {}", method.getName());
                    bindCallbackQueryHandler(bean, method, userId);
                }
            }
        }

        try {
            Method helpMethod = TelegramBotService.class.getMethod("helpMethod");
            TelegramCommand command = AnnotatedElementUtils.findMergedAnnotation(helpMethod, TelegramCommand.class);
            if (command != null) {
                for (String cmd : command.commands()) {
                    addHandlers(userId, t -> t.getCommandList()
                            .put(cmd, new TelegramHandler(beanFactory.getBean(TelegramBotService.class), helpMethod, command))
                    );
                }
            }
        }
        catch (Exception e) {
        }
    }

    private void bindDefaultMessageHandler(Object bean, Method method, OptionalLong userId) {
        log.debug("Bind TelegramBot message controller: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );
        addHandlers(userId, t -> t.setDefaultMessageHandler(new TelegramHandler(bean, method, null)));
    }

    private void bindCallbackQueryHandler(Object bean, Method method, OptionalLong userId) {
        log.debug("Bind TelegramBot callback query controller: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );

        TelegramCallbackQuery cbq = AnnotatedElementUtils.findMergedAnnotation(method, TelegramCallbackQuery.class);

        if (cbq.callbackData().length == 0)
            addHandlers(userId, t -> t.setDefaultCallbackQueryHandler(new TelegramHandler(bean, method, null)));
        else {
            for (String data : cbq.callbackData())
                addHandlers(userId, t -> t.getCallbackQueryList().put(data, new TelegramHandler(bean, method, null)));
        }
    }

    private void bindCommandHandler(Object bean, Method method, OptionalLong userId) {
        TelegramCommand command = AnnotatedElementUtils.findMergedAnnotation(method, TelegramCommand.class);
        if (command != null) {
            log.debug("Bind TelegramBot command controller: {}:{} for {}", bean.getClass(), method.getName(), userId);

            for (String cmd : command.commands()) {
                TelegramHandler telegramHandler = new TelegramHandler(bean, method, command);
//                if (cmd.endsWith(patternCommandSuffix)) {
//                    createOrGet(userId).getPatternCommandList()
//                            .put(cmd.substring(0, cmd.length() - patternCommandSuffix.length()), telegramHandler);
//                }
//                else {
//                }
                addHandlers(userId, t -> t.getCommandList().put(cmd, telegramHandler));
            }
        }
    }

    private void bindForwardHandler(Object bean, Method method, OptionalLong userId) {
        log.debug("Bind TelegramBot forward controller: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );
//        telegramBotService.addForwardMessageHandler(bean, method, userId);
        addHandlers(userId, t -> t.setDefaultForwardHandler(new TelegramHandler(bean, method, null)));
    }

    private void bindHelpPrefix(Object bean, Method method, OptionalLong userId) {
        log.debug("Bind TelegramBot help prefix method: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );

        try {
            String help = method.invoke(bean).toString();
            addHandlers(userId, t -> t.setPrefixHelpMessage(help));
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("Cannot set help prefix: {}", e.getMessage());
        }
    }
}
