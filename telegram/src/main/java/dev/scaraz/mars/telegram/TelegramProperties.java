package dev.scaraz.mars.telegram;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static dev.scaraz.mars.telegram.TelegramBotProperties.DEFAULT_MAX_THREADS;

@Data
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {
    private boolean enabled = true;
    private TelegramBotType type = TelegramBotType.LONG_POLLING;
    private String name;
    private String token;
    private int maxThreads = DEFAULT_MAX_THREADS;
}
