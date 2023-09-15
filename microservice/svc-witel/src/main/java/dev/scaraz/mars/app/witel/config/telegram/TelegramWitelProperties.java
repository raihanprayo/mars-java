package dev.scaraz.mars.app.witel.config.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Getter
@Setter
@ConfigurationProperties(prefix = "mars.telegram")
public class TelegramWitelProperties {
    private String token;
    private int maxThreads = 2;

    private String proxyHost;
    private int proxyPort;
    private DefaultBotOptions.ProxyType proxyType = DefaultBotOptions.ProxyType.NO_PROXY;
}
