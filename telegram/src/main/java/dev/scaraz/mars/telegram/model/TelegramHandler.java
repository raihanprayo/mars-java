package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.Optional;

@Builder
@ToString
@RequiredArgsConstructor
public class TelegramHandler {
    private final Object bean;
    private final Method method;
    private final TelegramCommand telegramCommand;

    public Object getBean() {
        return bean;
    }
    public Class<?> getBeanClass() {
        return bean.getClass();
    }

    public Method getMethod() {
        return method;
    }
    public String getMethodName() {
        return method.getName();
    }

    public Optional<TelegramCommand> getTelegramCommand() {
        return Optional.ofNullable(telegramCommand);
    }

}
