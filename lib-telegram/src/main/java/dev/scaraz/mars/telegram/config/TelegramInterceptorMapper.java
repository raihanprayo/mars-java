package dev.scaraz.mars.telegram.config;

import dev.scaraz.mars.telegram.model.TelegramInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import jakarta.annotation.Priority;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Priority(Integer.MAX_VALUE)
public class TelegramInterceptorMapper implements BeanPostProcessor {

    private final List<TelegramInterceptor> interceptors = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (ClassUtils.isAssignable(TelegramInterceptor.class, bean.getClass())) {
            log.debug("ADD INTERCEPTOR - {}", bean.getClass().getName());
            interceptors.add((TelegramInterceptor) bean);
        }
        return bean;
    }

    public List<TelegramInterceptor> getInterceptors() {
        return interceptors;
    }
}

