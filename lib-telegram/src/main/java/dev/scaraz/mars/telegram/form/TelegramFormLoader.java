package dev.scaraz.mars.telegram.form;

import dev.scaraz.mars.telegram.TelegramProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "telegram.form",
        name = "enabled",
        havingValue = "true")
public class TelegramFormLoader implements InitializingBean {

    private final TelegramProperties telegramProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

}
