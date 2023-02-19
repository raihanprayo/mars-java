package dev.scaraz.mars.telegram;

import lombok.*;

/**
 * Builder for Telegram Bot API. Should be provided as bean.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */

@Data
@Builder
@AllArgsConstructor
public class TelegramBotProperties {

    public static final int DEFAULT_MAX_THREADS = 30;

    private String username;
    private String token;
    private String path;

    @Builder.Default
    private int maxThreads = DEFAULT_MAX_THREADS;

}
