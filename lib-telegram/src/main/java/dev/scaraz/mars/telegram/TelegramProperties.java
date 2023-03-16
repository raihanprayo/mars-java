package dev.scaraz.mars.telegram;

import dev.scaraz.mars.telegram.util.enums.BotType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static dev.scaraz.mars.telegram.TelegramBotProperties.DEFAULT_MAX_THREADS;

@Data
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {
    private boolean enabled = true;
    private boolean formEnabled = false;

    private String name;
    private String token;

    private BotType type = BotType.LONG_POLLING;

    private final AsyncPool async = new AsyncPool();

    @Getter
    @Setter
    public static class AsyncPool {
        private int corePoolSize = 2;
        private int maxPoolSize = 30;
        private int queueCapacity = 1000;
    }
}
