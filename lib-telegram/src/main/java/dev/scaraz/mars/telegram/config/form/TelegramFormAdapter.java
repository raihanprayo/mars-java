package dev.scaraz.mars.telegram.config.form;

import dev.scaraz.mars.telegram.config.TelegramFormRegistry;
import dev.scaraz.mars.telegram.model.form.FormStructure;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

public abstract class TelegramFormAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final List<FormStructure> forms = new ArrayList<>();
    private final TelegramFormRegistry registry = new TelegramFormRegistry(forms);

    @Override
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.configure(registry);
    }

    public void configure(TelegramFormRegistry registry) {
    }

}

