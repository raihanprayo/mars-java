package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import lombok.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TelegramProcessContext {

    private TelegramProcessor processor;

    private TelegramHandler handler;
    private BotApiMethod<?> result;

    public boolean hasResult() {
        return result != null;
    }
}
