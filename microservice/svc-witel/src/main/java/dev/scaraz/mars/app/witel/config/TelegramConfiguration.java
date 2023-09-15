package dev.scaraz.mars.app.witel.config;

import dev.scaraz.mars.app.witel.config.telegram.TelegramApi;
import dev.scaraz.mars.app.witel.config.telegram.TelegramApiOptions;
import dev.scaraz.mars.app.witel.config.telegram.TelegramWitelProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Configuration
@RequiredArgsConstructor
public class TelegramConfiguration {

    private final TelegramWitelProperties telegramWitelProperties;

    @Bean
    public TelegramApi telegramApi() {
        Assert.isTrue(StringUtils.isNotBlank(telegramWitelProperties.getToken()), "bot token is empty");

        TelegramApiOptions options = new TelegramApiOptions();
        options.setMaxThreads(telegramWitelProperties.getMaxThreads());
        options.setProxyType(telegramWitelProperties.getProxyType());
        options.setProxyHost(telegramWitelProperties.getProxyHost());
        options.setProxyPort(telegramWitelProperties.getProxyPort());
        return new TelegramApi(options);
    }

}
