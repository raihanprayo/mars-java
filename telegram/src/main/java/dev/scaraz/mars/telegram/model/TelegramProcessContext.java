package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.config.processor.TelegramProcessor;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TelegramProcessContext {
    private Update update;
    private TelegramHandler handler;
    private TelegramProcessor processor;
    private BotApiMethod<?> result;

    public boolean hasResult() {
        return result != null;
    }

    public long getId() {
        return update.getUpdateId();
    }

    public HandlerType getType() {
        return processor.type();
    }

}
