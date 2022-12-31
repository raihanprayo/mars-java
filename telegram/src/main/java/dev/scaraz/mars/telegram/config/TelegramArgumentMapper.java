package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.model.*;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor

@Component
public class TelegramArgumentMapper implements BeanPostProcessor {
    private static final Function<HandlerType, Set<TelegramArgResolver>> DEFAULT_HANDLER_SET = t -> new HashSet<>();

    private final TelegramBotsApi api;

    private final ApplicationContext applicationContext;

    private final Map<HandlerType, Set<TelegramArgResolver>> paramTypeResolvers = new EnumMap<>(HandlerType.class);

//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if (bean instanceof TelegramArgResolver) {
//            log.debug("ADD RESOLVER FROM BEAN PROCESSOR {}", beanName);
//            addParamResolver((TelegramArgResolver) bean);
//        }
//        return bean;
//    }

    public void addParamResolver(TelegramArgResolver... resolvers) {
        for (TelegramArgResolver resolver : resolvers) {
            boolean canBeResolve = resolver instanceof TelegramAnnotationArgResolver ||
                    resolver instanceof TelegramTypeArgResolver;

            if (!canBeResolve) continue;
            List<HandlerType> handlerTypes = resolver.handledFor();

            if (handlerTypes.size() == 0) {
                paramTypeResolvers.computeIfAbsent(HandlerType.ALL, DEFAULT_HANDLER_SET)
                        .add(resolver);
            }
            else {
                for (HandlerType handlerType : handlerTypes) {
                    paramTypeResolvers.computeIfAbsent(handlerType, DEFAULT_HANDLER_SET)
                            .add(resolver);
                }
            }
        }
    }

    public Object resolveMethodArg(int index,
                                   Method method,
                                   TelegramHandlerContext context,
                                   Update update,
                                   @Nullable
                                   TelegramMessageCommand messageCommand
    ) {
        MethodParameter mp = new MethodParameter(method, index);
        Class<?> parameterType = mp.getParameterType();

        log.debug("GET PARAMETER ARG ({}.{}) AT INDEX {}", method.getDeclaringClass().getSimpleName(), method.getName(), index);
        for (HandlerType handlerType : List.of(context.getScope(), HandlerType.ALL)) {
            if (mp.hasParameterAnnotations()) {
                log.debug(" - resolving argument by applied annotation");
                Optional<TelegramArgResolver> resolverOptional = paramTypeResolvers.get(handlerType).stream()
                        .filter(r -> r instanceof TelegramAnnotationArgResolver)
                        .filter(r -> hasSupportedAnnotations(mp, r))
                        .findFirst();

                if (resolverOptional.isPresent()) {
                    Object value = resolverOptional.get().resolve(mp, context, update, messageCommand);
                    if (ClassUtils.isAssignable(value.getClass(), parameterType)) return value;

                    throw new IllegalArgumentException(String.format("Invalid parameter type (%s), resolver return type (%s) are different from declared type.",
                            parameterType,
                            value.getClass()
                    ));
                }
            }
            else {
                log.debug(" - resolving argument by return type");
                Optional<? extends TelegramTypeArgResolver<?>> resolverByType = paramTypeResolvers.get(handlerType).stream()
                        .filter(r -> r instanceof TelegramTypeArgResolver)
                        .map(r -> (TelegramTypeArgResolver<?>) r)
                        .filter(r -> ClassUtils.isAssignable(parameterType, r.getType()))
                        .findFirst();

                if (resolverByType.isPresent()) {
                    return resolverByType.get().resolve(mp, context, update, messageCommand);
                }
//                for (TelegramTypeArgResolver<?> resolver : resolverByType) {
//
//                    Object value = resolver.resolve(mp, context, update, messageCommand);
//                    if (ClassUtils.isAssignable(value.getClass(), parameterType)) return value;
//                }
            }
        }

        throw new IllegalArgumentException(String.format(
                "Unresolved argument type (%s) for method %s.%s at index %s",
                parameterType,
                method.getDeclaringClass().getSimpleName(),
                method.getName(),
                index
        ));
    }

    public Object[] makeArgumentList(
            TelegramBotService service,
            TelegramHandler handler,
            Update update,
            HandlerType handlerType,
            @Nullable TelegramMessageCommand command
    ) throws IllegalArgumentException {
        TelegramHandlerContext context = TelegramHandlerContext.builder()
                .scope(handlerType)
                .api(api)
                .service(service)
                .command(handler.getTelegramCommand().orElse(null))
                .build();

        return IntStream.range(0, handler.getMethod().getParameterTypes().length)
                .boxed()
                .map(index -> resolveMethodArg(
                        index, handler.getMethod(),
                        context, update, command)
                )
                .toArray();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        Map<String, TelegramArgResolver> beans = applicationContext.getBeansOfType(TelegramArgResolver.class);
        for (Map.Entry<String, TelegramArgResolver> entry : beans.entrySet()) {
            String beanName = entry.getKey();
            TelegramArgResolver resolver = entry.getValue();
            log.debug("ADD RESOLVER {} -- {}", beanName, resolver);
            addParamResolver(resolver);
        }
    }

    private boolean hasSupportedAnnotations(MethodParameter mp, TelegramArgResolver resolver) {
        if (resolver instanceof TelegramAnnotationArgResolver)
            return ((TelegramAnnotationArgResolver) resolver).supportedAnnotations().stream()
                    .anyMatch(mp::hasParameterAnnotation);
        return false;
    }

    private Predicate<TelegramArgResolver> isAnnotationArgResolver(boolean b) {
        return r -> r instanceof TelegramAnnotationArgResolver;
    }

}
