package dev.scaraz.mars.telegram;

import dev.scaraz.mars.telegram.util.enums.BotType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static dev.scaraz.mars.telegram.TelegramBotProperties.DEFAULT_MAX_THREADS;

@Data
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {
    private boolean enabled = true;
    private String name;
    private String token;

    private BotType type = BotType.LONG_POLLING;
    private int maxThreads = DEFAULT_MAX_THREADS;
}
