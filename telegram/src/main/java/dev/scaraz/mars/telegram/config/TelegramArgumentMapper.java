package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.annotation.Command;
import dev.scaraz.mars.telegram.annotation.Text;
import dev.scaraz.mars.telegram.annotation.UserId;
import dev.scaraz.mars.telegram.model.TelegramArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
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

    private final Map<HandlerType, Set<TelegramArgResolver>> paramTypeResolvers = new EnumMap<>(HandlerType.class);

    @PostConstruct
    private void init() {
        initDefaultResolvers();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (TelegramArgResolver.class.isAssignableFrom(bean.getClass())) {
            addParamResolver((TelegramArgResolver) bean);
        }
        return bean;
    }

    public void addParamResolver(TelegramArgResolver... resolvers) {
        for (TelegramArgResolver resolver : resolvers) {
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

        for (HandlerType handlerType : List.of(context.getScope(), HandlerType.ALL)) {
            if (mp.hasParameterAnnotations()) {
                Optional<TelegramArgResolver> resolverOptional = paramTypeResolvers.get(handlerType).stream()
                        .filter(isAnnotationArgResolver(true))
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
                Set<TelegramArgResolver> resolverByType = paramTypeResolvers.get(handlerType).stream()
                        .filter(isAnnotationArgResolver(false))
                        .collect(Collectors.toSet());

                for (TelegramArgResolver resolver : resolverByType) {
                    Object value = resolver.resolve(mp, context, update, messageCommand);
                    if (ClassUtils.isAssignable(value.getClass(), parameterType)) return value;
                }
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

    private boolean hasSupportedAnnotations(MethodParameter mp, TelegramArgResolver resolver) {
        return resolver.supportedAnnotations().stream()
                .anyMatch(mp::hasParameterAnnotation);
    }

    private Predicate<TelegramArgResolver> isAnnotationArgResolver(boolean b) {
        return r -> {
            int size = r.supportedAnnotations().size();
            return b ? (size == 0) : (size > 0);
        };
    }

    private void initDefaultResolvers() {
        TelegramArgResolver updateResolver = (mp, context, update, messageCommand) -> update;
        TelegramArgResolver botResolver = (mp, context, update, messageCommand) -> context.getService().getClient();
        TelegramArgResolver apiResolver = (mp, context, update, messageCommand) -> context.getApi();

        TelegramArgResolver messageResolver = (mp, context, update, messageCommand) ->
                context.getScope() == HandlerType.CALLBACK_QUERY ?
                        update.getCallbackQuery().getMessage() : update.getMessage();

        TelegramArgResolver userResolver = (mp, context, update, messageCommand) ->
                context.getScope() == HandlerType.MESSAGE ?
                        update.getMessage().getFrom() : update.getCallbackQuery().getFrom();

        TelegramArgResolver cbQueryResolver = new TelegramArgResolver() {
            @Override
            public List<HandlerType> handledFor() {
                return List.of(HandlerType.CALLBACK_QUERY);
            }

            @Override
            public Object resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
                return update.getCallbackQuery();
            }

        };

        TelegramArgResolver textAnnotResolver = new TelegramArgResolver() {
            @Override
            public List<Class<? extends Annotation>> supportedAnnotations() {
                return List.of(Text.class);
            }

            @Override
            public Object resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
                switch (ctx.getScope()) {
                    case MESSAGE:
                        return mc.getArgument().orElse("");
                    case CALLBACK_QUERY:
                        return update.getCallbackQuery().getMessage().getText();
                }
                return null;
            }
        };

        TelegramArgResolver cmdAnnotResolver = new TelegramArgResolver() {
            @Override
            public List<Class<? extends Annotation>> supportedAnnotations() {
                return List.of(Command.class);
            }

            @Override
            public List<HandlerType> handledFor() {
                return List.of(HandlerType.MESSAGE);
            }

            @Override
            public Object resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
                return mc.getCommand().orElse("");
            }

        };

        TelegramArgResolver usrIdAnnotResolver = new TelegramArgResolver() {
            @Override
            public List<Class<? extends Annotation>> supportedAnnotations() {
                return List.of(UserId.class);
            }

            @Override
            public Object resolve(MethodParameter mp, TelegramHandlerContext ctx, Update update, @Nullable TelegramMessageCommand mc) {
                switch (ctx.getScope()) {
                    case MESSAGE:
                        return update.getMessage().getFrom().getId();
                    case CALLBACK_QUERY:
                        return update.getCallbackQuery().getFrom().getId();
                }
                return null;
            }

        };

        addParamResolver(
                updateResolver,
                botResolver,
                apiResolver,
                messageResolver,
                userResolver,
                cbQueryResolver,
                textAnnotResolver,
                cmdAnnotResolver,
                usrIdAnnotResolver
        );

    }

}
