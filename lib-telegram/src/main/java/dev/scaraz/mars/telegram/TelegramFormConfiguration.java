package dev.scaraz.mars.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = "telegram.form",
        name = "enabled",
        havingValue = "true")
@RequiredArgsConstructor
public class TelegramFormConfiguration {
}
