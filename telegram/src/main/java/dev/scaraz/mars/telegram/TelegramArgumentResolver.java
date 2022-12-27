package dev.scaraz.mars.telegram;

import dev.scaraz.mars.telegram.model.TelegramArgResolver;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramHandlerContext;
import dev.scaraz.mars.telegram.model.TelegramMessageCommand;
import dev.scaraz.mars.telegram.service.resolver.*;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@Component
public class TelegramArgumentResolver implements BeanPostProcessor {

    private final ConfigurableBeanFactory beanFactory;

    private final List<TelegramArgResolver<?>> paramTypeResolvers = new ArrayList<>();



    @PostConstruct
    private void init() {
        initDefaultResolvers();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (TelegramArgResolver.class.isAssignableFrom(bean.getClass())) {
            TelegramArgResolver<?> resolver = (TelegramArgResolver<?>) bean;
            paramTypeResolvers.add(resolver);
        }
        return bean;
    }

    public Optional<TelegramArgResolver<?>> getResolverByHandlerType(Type type, HandlerType... handlerTypes) {
        List<HandlerType> types = List.of(handlerTypes);
        for (TelegramArgResolver<?> resolver : paramTypeResolvers) {
            if (types.contains(resolver.handledFor()) && resolver.getType() == type) {
                return Optional.of(resolver);
            }
        }
        return Optional.empty();
    }

    public Object[] makeArgumentList(TelegramHandler handler,
                                     Update update,
                                     HandlerType handlerType,
                                     @Nullable TelegramMessageCommand command) throws IllegalArgumentException {
        AtomicInteger integer = new AtomicInteger(0);
        return Stream.of(handler.getMethod().getGenericParameterTypes())
                .map(type -> {
                    final int index = integer.getAndIncrement();
                    return this.getResolverByHandlerType(type, HandlerType.BOTH, handlerType)
                            .orElseThrow(() -> new IllegalArgumentException(String.format(
                                    "Unresolved argument type (%s) for method %s.%s at index %s",
                                    type,
                                    handler.getBeanClass().getSimpleName(),
                                    handler.getMethodName(),
                                    index
                            )));
                })
                .map(resolver -> resolver.resolve(
                                TelegramHandlerContext.builder()
                                        .telegramCommand(handler.getTelegramCommand().orElse(null))
                                        .scope(handlerType)
                                        .bean(handler.getBean())
                                        .build(),
                                update,
                                command
                        )
                )
                .toArray();
    }

    private void initDefaultResolvers() {
        TelegramArgResolver<Update> updateResolver = (context, update, messageCommand) -> update;
        TelegramArgResolver<AbsSender> botResolver = (context, update, messageCommand) -> context.getService().getClient();
        TelegramArgResolver<TelegramBotsApi> apiResolver = (context, update, messageCommand) -> context.getApi();

        TelegramArgResolver<Message> messageResolver = (context, update, messageCommand) ->
                context.getScope() == HandlerType.CALLBACK_QUERY ?
                        update.getCallbackQuery().getMessage() : update.getMessage();

        TelegramArgResolver<User> userResolver = (context, update, messageCommand) ->
                context.getScope() == HandlerType.MESSAGE ?
                        update.getMessage().getFrom() : update.getCallbackQuery().getFrom();

        TelegramArgResolver<CallbackQuery> cbQueryResolver = new TelegramArgResolver<>() {
            @Override
            public HandlerType handledFor() {
                return HandlerType.CALLBACK_QUERY;
            }

            @Override
            public Object resolve(TelegramHandlerContext context, Update update, @Nullable TelegramMessageCommand messageCommand) {
                return update.getCallbackQuery();
            }
        };

        paramTypeResolvers.addAll(List.of(
                updateResolver,
                botResolver,
                apiResolver,
                messageResolver,
                userResolver,
                cbQueryResolver
        ));

    }
}
