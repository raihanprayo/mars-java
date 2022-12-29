package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.TelegramForward;
import dev.scaraz.mars.telegram.annotation.TelegramHelp;
import dev.scaraz.mars.telegram.annotation.TelegramMessage;

import dev.scaraz.mars.telegram.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

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
public class TelegramListenerResolver implements BeanPostProcessor {

    private final TelegramBotService telegramBotService;
    private final ConfigurableBeanFactory configurableBeanFactory;
    private EmbeddedValueResolver embeddedValueResolver;
    private final Map<String, Class<?>> botControllerMap = new HashMap<>();
    private final Map<OptionalLong, Map<String, Class<?>>> botControllerMapByUserId = new HashMap<>();

    @PostConstruct
    private void init() {
        this.embeddedValueResolver = new EmbeddedValueResolver(configurableBeanFactory);
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        TelegramBot telegramBot = AnnotatedElementUtils.findMergedAnnotation(beanClass, TelegramBot.class);
        if (telegramBot != null) {
            if (telegramBot.userId().length != 0) {
                for (String userId : telegramBot.userId()) {
                    String evalUserId = embeddedValueResolver.resolveStringValue(userId);
                    if (evalUserId == null) {
                        throw new RuntimeException("NPE at beanClass: " + beanClass + " on userId: " + userId);
                    }
                    for (String evaluatedUserId : evalUserId.split(",")) {
                        log.info("Init TelegramBot controller: {} for userId: {}", beanClass, userId);
                        botControllerMapByUserId.computeIfAbsent(OptionalLong.of(Long.parseLong(evaluatedUserId)), key -> new HashMap<>())
                                .put(beanName, beanClass);
                    }
                }
            }
            else {
                log.info("Init TelegramBot controller: {}", beanClass);
                botControllerMap.put(beanName, beanClass);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, String beanName) throws BeansException {
        bindControllers(bean, beanName, botControllerMap.get(beanName), OptionalLong.empty());
        botControllerMapByUserId.forEach((userId, original) ->
                bindControllers(bean, beanName, original.get(beanName), userId)
        );
        return bean;
    }

    private void bindControllers(@NonNull Object bean, String beanName, Class<?> original, OptionalLong userId) {
        if (original != null) {
            log.debug("Processing class {} as bean {} for user {}",
                    bean.getClass(), beanName, userId
            );
            for (Method method : original.getMethods()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }
                log.debug("Found method {}", method.getName());
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramCommand.class)) {
                    bindCommandController(bean, method, userId);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramMessage.class)) {
                    bindMessageController(bean, method, userId);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramForward.class)) {
                    bindForwardController(bean, method, userId);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramHelp.class)) {
                    bindHelpPrefix(bean, method, userId);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramCallbackQuery.class)) {
                    bindCallbackQueryController(bean, method, userId);
                }
            }
        }
        telegramBotService.addHelpMethod(userId);
    }

    private void bindMessageController(Object bean, Method method, OptionalLong userId) {
        log.debug("Init TelegramBot message controller: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );
        telegramBotService.addDefaultMessageHandler(bean, method, userId);
    }

    private void bindCallbackQueryController(Object bean, Method method, OptionalLong userId) {
        log.debug("Init TelegramBot callback query controller: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );
        telegramBotService.addDefaultCallbackQueryHandler(bean, method, userId);
    }

    private void bindCommandController(Object bean, Method method, OptionalLong userId) {
        log.debug("Init TelegramBot command controller: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );
        telegramBotService.addHandler(bean, method, userId);
    }

    private void bindForwardController(Object bean, Method method, OptionalLong userId) {
        log.debug("Init TelegramBot forward controller: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );
        telegramBotService.addForwardMessageHandler(bean, method, userId);
    }

    private void bindHelpPrefix(Object bean, Method method, OptionalLong userId) {
        log.debug("Init TelegramBot help prefix method: {}:{} for {}",
                bean.getClass(), method.getName(), userId
        );
        telegramBotService.addHelpPrefixMethod(bean, method, userId);
    }
}
